package de.saar.coli.ccgparser;

import java.util.List;

public class OutsideEstimator {
    private double[] bestSupertagScore;
    private int n;

    public OutsideEstimator(WordWithSupertags[] sentence) {
        bestSupertagScore = new double[sentence.length];
        n = sentence.length;

        for (int i = 0; i < sentence.length; i++) {
            WordWithSupertags word = sentence[i];
            bestSupertagScore[i] = word.supertags.get(0).score;
        }
    }

    // TODO - precompute these
    public double estimate(int start, int end) {
        double ret = 0;

        for( int i = 0; i < start; i++ ) {
            ret += bestSupertagScore[i];
        }

        for( int i = end; i < n; i++ ) {
            ret += bestSupertagScore[i];
        }

        return ret;
    }

    public double estimate(Item item) {
        return estimate(item.getStart(), item.getEnd());
    }
}
