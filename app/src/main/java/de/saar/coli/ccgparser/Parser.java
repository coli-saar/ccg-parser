package de.saar.coli.ccgparser;

import java.util.List;

public class Parser {
    private Agenda agenda;

    public Parser(WordWithSupertags[] sentence) {
        OutsideEstimator estimator = new OutsideEstimator(sentence);
        agenda = new Agenda(estimator);

        // fill agenda with supertag items
        int i = 0;
        for (WordWithSupertags word : sentence) {
            for( SupertagWithScore supertag : word.supertags ) {
                Item it = new Item(i, i+1, supertag.getCategory(), supertag.score);
                agenda.enqueue(it);
            }
            i++;
        }

        System.out.println(agenda);
    }
}
