package de.saar.coli.ccgparser;

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

    private boolean isGoalItem(Item item) {
        return item.getStart() == 0 && item.getEnd() == n && "S".equals(item.getCategory().getAtomic());
    }

    public void parse() {
        while( ! agenda.isEmpty() ) {
            System.err.println();
            System.err.println(agenda);

            Item item = agenda.dequeue();

            System.err.printf("Dequeued: %s\n", item.toString(estimator));

            // check for goal item
            if( isGoalItem(item) ) {
                System.err.println("** FOUND GOAL ITEM **");
                return;
            }

            // TODO - implement combinatory rules other than application

            // item acts as functor
            switch( item.getCategory().getType() ) {
                case FORWARD:
                    for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                        if( item.getCategory().getArgument().equals(partner.getCategory())) {
                            Item resultItem = new Item(item.getStart(), partner.getEnd(), item.getCategory().getFunctor(), item.getScore() + partner.getScore());
                            add(resultItem);
                        }
                    }
                    break;

                case BACKWARD:
                    for( Item partner : chart.getItemsWithEnd(item.getStart())) {
                        if( item.getCategory().getArgument().equals(partner.getCategory())) {
                            Item resultItem = new Item(partner.getStart(), item.getEnd(), item.getCategory().getFunctor(), item.getScore() + partner.getScore());
                            add(resultItem);
                        }
                    }
                    break;
            }

            // item acts as argument
            for( Item partner : chart.getItemsWithEnd(item.getStart())) {
                Category partnerCat = partner.getCategory();
                if( partnerCat.getType() == Category.CategoryType.FORWARD && partnerCat.getArgument().equals(item.getCategory())) {
                    Item resultItem = new Item(partner.getStart(), item.getEnd(), partnerCat.getFunctor(), item.getScore() + partner.getScore());
                    add(resultItem);
                }
            }

            for( Item partner : chart.getItemsWithStart(item.getEnd())) {
                Category partnerCat = partner.getCategory();
                if( partnerCat.getType() == Category.CategoryType.BACKWARD && partnerCat.getArgument().equals(item.getCategory())) {
                    Item resultItem = new Item(item.getStart(), partner.getEnd(), partnerCat.getFunctor(), item.getScore() + partner.getScore());
                    add(resultItem);
                }
            }
        }
    }
}
