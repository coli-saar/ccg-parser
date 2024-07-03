package de.saar.coli.ccgparser;

import com.google.common.base.Joiner;
import de.saar.coli.ccgparser.rules.*;
import de.up.ling.tree.Tree;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private Agenda agenda;
    private Chart chart;
    private OutsideEstimator estimator;
    private int n;
    private UnaryRules unaryRules;
    private WordWithSupertags[] sentence;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_TO_FILE = false;

    private static final CombinatoryRule TYPECHANGE = new Typechange();
    public static final CombinatoryRule[] COMBINATORY_RULES = new CombinatoryRule[] {
        new ForwardApplication(),
        new BackwardApplication(),
        new ForwardHarmonicComposition(),
        new BackwardHarmonicComposition()
    //        ,
    //    new ForwardCrossedComposition(),
    //    new BackwardCrossedComposition()
    };

    public Parser(WordWithSupertags[] sentence, UnaryRules unaryRules) throws IOException {
        if( DEBUG_TO_FILE ) {
            File file = new File("err.txt");
            FileOutputStream fos = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fos);
            System.setErr(ps);
        }

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
        Item canonicalItem = chart.getCanonicalItem(item);

        if( canonicalItem == null ) {
            // no "equals" item was already in the chart => add it
            if(DEBUG) System.err.printf("Enqueued: %s\n", item.toString(estimator));
            agenda.enqueue(item);
            chart.add(item);
        } else if( item.getScore() > canonicalItem.getScore() ) {
            // new item is "equals" to a previously discovered item and has better
            // score => update canonical item and decreaseKey it in the agenda
            if(DEBUG) System.err.printf("DecreaseKey: %s previously known with score %f\n", item.toString(estimator), canonicalItem.getScore());
            chart.updateCanonicalItem(item);
            agenda.decreaseKey(item);
        } else {
            // new item was known with a better score => do nothing
            if(DEBUG) System.err.printf("Already known: %s\n", item.toString(estimator));
        }

//
//
//
//        if( chart.contains(item) ) {
//            if(DEBUG) System.err.printf("Already known: %s\n", item.toString(estimator));
//        } else {
//            agenda.enqueue(item);
//            chart.add(item);
//            if(DEBUG) System.err.printf("Enqueued: %s\n", item.toString(estimator));
//        }
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
        SentenceStatistics stats = new SentenceStatistics();
        stats.length = n;
        stats.sentence = Joiner.on(" ").join(sentence);

        if(DEBUG) System.err.printf("\nCHART:\n" + chart + "\n");

        while( ! agenda.isEmpty() ) {
            if(DEBUG) System.err.printf("\nAgenda:\n%s\n", agenda);

            Item item = agenda.dequeue();
            Item foundGoalItem = null;

            if(DEBUG) System.err.printf("Dequeued: %s\n", item.toString(estimator));

            // item acts as functor
            for( CombinatoryRule rule : COMBINATORY_RULES ) {
                if( rule.isForward() ) {
                    for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                        Item newItem = rule.combine(item, partner);
                        if( newItem != null ) {
                            if(DEBUG) System.err.printf("[%s] %s <- <%s> / %s\n", rule, newItem, item, partner);
                            add(newItem);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                            stats.ruleCounts.put(rule, stats.ruleCounts.getOrDefault(rule, 0) + 1);
                        }
                    }
                } else {
                    for( Item partner : chart.getItemsWithEnd(item.getStart())) {
                        Item newItem = rule.combine(item, partner);
                        if( newItem != null ) {
                            if(DEBUG) System.err.printf("[%s] %s <- %s / <%s>\n", rule, newItem, item, partner);
                            add(newItem);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                            stats.ruleCounts.put(rule, stats.ruleCounts.getOrDefault(rule, 0) + 1);
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
                            if(DEBUG) System.err.printf("[%s] %s <- <%s> \\ %s\n", rule, newItem, item, partner);
                            add(newItem);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                            stats.ruleCounts.put(rule, stats.ruleCounts.getOrDefault(rule, 0) + 1);
                        }
                    }
                } else {
                    for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                        Item newItem = rule.combine(partner, item);
                        if( newItem != null ) {
                            if(DEBUG) System.err.printf("[%s] %s <- %s \\ <%s>\n", rule, newItem, item, partner);
                            add(newItem);
                            foundGoalItem = isGoalItem(newItem) ? newItem : foundGoalItem;
                            stats.ruleCounts.put(rule, stats.ruleCounts.getOrDefault(rule, 0) + 1);
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
                // System.err.println("** ENQUEUED GOAL ITEM **");

                stats.couldParse = true;
                stats.recordEndTime();
                for( StatisticsListener listener : listeners ) {
                    listener.accept(stats);
                }

                return makeParseTree(foundGoalItem);
            }
        }

        stats.couldParse = false;
        stats.recordEndTime();
        for( StatisticsListener listener : listeners ) {
            listener.accept(stats);
        }
        return null;
    }

    public static class SentenceStatistics {
        public Map<CombinatoryRule,Integer> ruleCounts = new HashMap<>();
        public boolean couldParse = false;
        public int length = 0;
        public String sentence = null;
        public long parsingTimeNano = System.nanoTime();

        public void recordEndTime() {
            parsingTimeNano = System.nanoTime() - parsingTimeNano;
        }
    }

    public static interface StatisticsListener {
        public void accept(SentenceStatistics stats);
        public void close();
    }

    private List<StatisticsListener> listeners = new ArrayList<>();
    public void addStatisticsListener(StatisticsListener listener) {
        listeners.add(listener);
    }
}
