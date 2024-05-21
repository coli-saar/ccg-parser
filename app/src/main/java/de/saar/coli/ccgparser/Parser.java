package de.saar.coli.ccgparser;

import de.up.ling.tree.Tree;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.List;

public class Parser {
    private Agenda agenda;
    private Chart chart;
    private OutsideEstimator estimator;
    private int n;
    private UnaryRules unaryRules;
    private WordWithSupertags[] sentence;
    private static final boolean DEBUG = false;

    // This would be nice, but it adds 30% overhead to the parsing time, so let's not.
//    private static final Logger logger = LogManager.getLogger("Parser");
//
//    static {
//        // log only selected messages
//        Configurator.setLevel(logger.getName(), Level.INFO);
//
//        // clean up the layout of the logger - the crucial part is the PatternLayout
//        LoggerContext context = (LoggerContext) LogManager.getContext(false);
//        Configuration config = context.getConfiguration();
//        PatternLayout layout = PatternLayout.newBuilder().withPattern("[P] %m\n").build();
//        ConsoleAppender appender = ConsoleAppender.newBuilder().setName("Clean").setLayout(layout).setTarget(ConsoleAppender.Target.SYSTEM_ERR).build();
//        appender.start();
//        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
//        for (Appender oldAppender : loggerConfig.getAppenders().values()) {
//            loggerConfig.removeAppender(oldAppender.getName());
//        }
//        loggerConfig.addAppender(appender, null, null);
//        context.updateLoggers();
//    }

    public Parser(WordWithSupertags[] sentence, UnaryRules unaryRules) {
        n = sentence.length;
        estimator = new OutsideEstimator(sentence);
        agenda = new Agenda(estimator);
        chart = new Chart(sentence.length);
        this.unaryRules = unaryRules;
        this.sentence = sentence;

        // fill agenda with supertag items
        int i = 0;
        for (WordWithSupertags word : sentence) {
            for( SupertagWithScore supertag : word.supertags ) {
                Item it = new Item(i, i+1, supertag.getCategory(), supertag.score);
                add(it);
            }
            i++;
        }


    }

    private void add(Item item) {
        if( chart.contains(item) ) {
            if(DEBUG) System.err.printf("Already known: %s\n", item.toString(estimator));
        } else {
            agenda.enqueue(item);
            chart.add(item);
            if(DEBUG) System.err.printf("Enqueued: %s\n", item.toString(estimator));
        }
    }

    private Item create(int start, int end, Category category, Item functor, Item argument, CombinatoryRule combinatoryRule) {
        Item resultItem = new Item(start, end, category, functor.getScore() + argument.getScore());
        resultItem.addBackpointer(new Backpointer(List.of(functor, argument), combinatoryRule));
        add(resultItem);
        return resultItem;
    }

    private boolean isGoalItem(Item item) {
        String atomic = item.getCategory().getAtomic();
        return item.getStart() == 0 && item.getEnd() == n && atomic != null && atomic.startsWith("S");
    }

    private Tree<String> makeParseTree(Item item) {
        if( item.getBackpointers().isEmpty() ) {
            return Tree.create(item.getCategory().toString(), Tree.create(sentence[item.getStart()].word));
        } else {
            Backpointer bp = item.getBackpointers().get(0);
            if( bp.getPieces().size() == 1 ) {
                // type-changing rule
                return Tree.create(bp.getCombinatoryRule().getSymbol() + ":" + bp.getAnnotation(), makeParseTree(bp.getPieces().get(0)));
            } else if( bp.getCombinatoryRule().isForward() ) {
                // forward
                return Tree.create(bp.getCombinatoryRule().getSymbol(), makeParseTree(bp.getPieces().get(0)), makeParseTree(bp.getPieces().get(1)));
            } else {
                // backward
                return Tree.create(bp.getCombinatoryRule().getSymbol(), makeParseTree(bp.getPieces().get(1)), makeParseTree(bp.getPieces().get(0)));
            }
        }
    }

    public Tree<String> parse() {
        while( ! agenda.isEmpty() ) {
            if(DEBUG) System.err.printf("\n%s\n", agenda);

            Item item = agenda.dequeue();
            Item foundGoalItem = null;

            if(DEBUG) System.err.printf("Dequeued: %s\n", item.toString(estimator));

            // TODO - implement combinatory rules other than application

            // item acts as functor
            switch( item.getCategory().getType() ) {
                case FORWARD:
                    for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                        if( item.getCategory().getArgument().equals(partner.getCategory())) {
                            Item newItem = create(item.getStart(), partner.getEnd(), item.getCategory().getFunctor(), item, partner, CombinatoryRule.FORWARD_APPLICATION);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                        }
                    }
                    break;

                case BACKWARD:
                    for( Item partner : chart.getItemsWithEnd(item.getStart())) {
                        if( item.getCategory().getArgument().equals(partner.getCategory())) {
                            Item newItem = create(partner.getStart(), item.getEnd(), item.getCategory().getFunctor(), item, partner, CombinatoryRule.BACKWARD_APPLICATION);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                        }
                    }
                    break;
            }

            // item acts as argument
            for( Item partner : chart.getItemsWithEnd(item.getStart())) {
                Category partnerCat = partner.getCategory();
                if( partnerCat.getType() == Category.CategoryType.FORWARD && partnerCat.getArgument().equals(item.getCategory())) {
                    Item newItem = create(partner.getStart(), item.getEnd(), partnerCat.getFunctor(), partner, item, CombinatoryRule.FORWARD_APPLICATION);
                    foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                }
            }

            for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                Category partnerCat = partner.getCategory();
                if( partnerCat.getType() == Category.CategoryType.BACKWARD && partnerCat.getArgument().equals(item.getCategory())) {
                    Item newItem = create(item.getStart(), partner.getEnd(), partnerCat.getFunctor(), partner, item, CombinatoryRule.BACKWARD_APPLICATION);
                    foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                }
            }

            // process unary type-changing rules
            for( Category changedCategory : unaryRules.get(item.getCategory())) {
                Item modified = new Item(item.getStart(), item.getEnd(), changedCategory, item.getScore());
                Backpointer bp = new Backpointer(List.of(item), CombinatoryRule.TYPECHANGE);
                bp.setAnnotation(changedCategory.toString());
                modified.addBackpointer(bp);
                add(modified);
                foundGoalItem = isGoalItem(modified) ? modified : foundGoalItem;
            }

            if( foundGoalItem != null ) {
                System.err.println("** ENQUEUED GOAL ITEM **");
                return makeParseTree(foundGoalItem);
            }
        }

        return null;
    }
}
