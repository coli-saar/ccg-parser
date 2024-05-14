package de.saar.coli.ccgparser;

import de.up.ling.tree.Tree;

import java.util.List;

public class Parser {
    private Agenda agenda;
    private Chart chart;
    private OutsideEstimator estimator;
    private int n;

    public Parser(WordWithSupertags[] sentence) {
        n = sentence.length;
        estimator = new OutsideEstimator(sentence);
        agenda = new Agenda(estimator);
        chart = new Chart(sentence.length);

        // fill agenda with supertag items
        int i = 0;
        for (WordWithSupertags word : sentence) {
            for( SupertagWithScore supertag : word.supertags ) {
                Item it = new Item(i, i+1, supertag.getCategory(), supertag.score);
                add(it);
            }
            i++;
        }

//        System.out.println(agenda);
    }

    private void add(Item item) {
        if( chart.contains(item) ) {
            System.err.printf("Already known: %s\n", item.toString(estimator));
        } else {
            agenda.enqueue(item);
            chart.add(item);
            System.err.printf("Enqueued: %s\n", item.toString(estimator));
        }
    }

    private void create(int start, int end, Category category, Item functor, Item argument, CombinatoryRule combinatoryRule) {
        Item resultItem = new Item(start, end, category, functor.getScore() + argument.getScore());
        resultItem.addBackpointer(new Backpointer(List.of(functor, argument), combinatoryRule));
        add(resultItem);
    }

    private boolean isGoalItem(Item item) {
        return item.getStart() == 0 && item.getEnd() == n && "S".equals(item.getCategory().getAtomic());
    }

    private Tree<String> makeParseTree(Item item) {
        if( item.getBackpointers().isEmpty() ) {
            return Tree.create(item.getCategory().toString());
        } else {
            Backpointer bp = item.getBackpointers().get(0);
            if( bp.getCombinatoryRule().isForward() ) {
                return Tree.create(bp.getCombinatoryRule().getSymbol(), makeParseTree(bp.getPieces().get(0)), makeParseTree(bp.getPieces().get(1)));
            } else {
                return Tree.create(bp.getCombinatoryRule().getSymbol(), makeParseTree(bp.getPieces().get(1)), makeParseTree(bp.getPieces().get(0)));
            }
        }
    }

    public Tree<String> parse() {
        while( ! agenda.isEmpty() ) {
            System.err.println();
            System.err.println(agenda);

            Item item = agenda.dequeue();

            System.err.printf("Dequeued: %s\n", item.toString(estimator));

            // check for goal item
            if( isGoalItem(item) ) {
                System.err.println("** FOUND GOAL ITEM **");
                return makeParseTree(item);
            }

            // TODO - implement combinatory rules other than application

            // item acts as functor
            switch( item.getCategory().getType() ) {
                case FORWARD:
                    for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                        if( item.getCategory().getArgument().equals(partner.getCategory())) {
                            create(item.getStart(), partner.getEnd(), item.getCategory().getFunctor(), item, partner, CombinatoryRule.FORWARD_APPLICATION);
                        }
                    }
                    break;

                case BACKWARD:
                    for( Item partner : chart.getItemsWithEnd(item.getStart())) {
                        if( item.getCategory().getArgument().equals(partner.getCategory())) {
                            create(partner.getStart(), item.getEnd(), item.getCategory().getFunctor(), item, partner, CombinatoryRule.BACKWARD_APPLICATION);
                        }
                    }
                    break;
            }

            // item acts as argument
            for( Item partner : chart.getItemsWithEnd(item.getStart())) {
                Category partnerCat = partner.getCategory();
                if( partnerCat.getType() == Category.CategoryType.FORWARD && partnerCat.getArgument().equals(item.getCategory())) {
                    create(partner.getStart(), item.getEnd(), partnerCat.getFunctor(), partner, item, CombinatoryRule.FORWARD_APPLICATION);
                }
            }

            for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                Category partnerCat = partner.getCategory();
                if( partnerCat.getType() == Category.CategoryType.BACKWARD && partnerCat.getArgument().equals(item.getCategory())) {
                    create(item.getStart(), partner.getEnd(), partnerCat.getFunctor(), partner, item, CombinatoryRule.BACKWARD_APPLICATION);
                }
            }
        }

        return null;
    }
}
