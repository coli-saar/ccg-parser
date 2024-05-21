package de.saar.coli.ccgparser;

import de.saar.coli.ccgparser.rules.*;
import de.up.ling.tree.Tree;

import java.util.List;

public class Parser {
    private Agenda agenda;
    private Chart chart;
    private OutsideEstimator estimator;
    private int n;
    private UnaryRules unaryRules;
    private WordWithSupertags[] sentence;
    private static final boolean DEBUG = false;

    private static final CombinatoryRule TYPECHANGE = new Typechange();
    private static final CombinatoryRule[] COMBINATORY_RULES = new CombinatoryRule[] {
        new ForwardApplication(),
        new BackwardApplication(),
        new ForwardHarmonicComposition(),
        new BackwardHarmonicComposition(),
        new ForwardCrossedComposition(),
        new BackwardCrossedComposition()
    };

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

            // item acts as functor
            for( CombinatoryRule rule : COMBINATORY_RULES ) {
                if( rule.isForward() ) {
                    for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                        Item newItem = rule.combine(item, partner);
                        if( newItem != null ) {
                            add(newItem);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                        }
                    }
                } else {
                    for( Item partner : chart.getItemsWithEnd(item.getStart())) {
                        Item newItem = rule.combine(item, partner);
                        if( newItem != null ) {
                            add(newItem);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                        }
                    }
                }
            }

            // item acts as argument
            for( CombinatoryRule rule : COMBINATORY_RULES ) {
                if( rule.isForward() ) {
                    for( Item partner : chart.getItemsWithEnd(item.getStart())) {
                        Item newItem = rule.combine(partner, item);
                        if( newItem != null ) {
                            add(newItem);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                        }
                    }
                } else {
                    for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                        Item newItem = rule.combine(partner, item);
                        if( newItem != null ) {
                            add(newItem);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                        }
                    }
                }
            }

            // process unary type-changing rules
            for( Category changedCategory : unaryRules.get(item.getCategory())) {
                Item modified = new Item(item.getStart(), item.getEnd(), changedCategory, item.getScore());
                Backpointer bp = new Backpointer(List.of(item), TYPECHANGE);
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
