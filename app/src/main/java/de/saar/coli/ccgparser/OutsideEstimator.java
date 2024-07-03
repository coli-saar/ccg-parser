package de.saar.coli.ccgparser;

import java.util.List;

public class OutsideEstimator {
    private double[] bestSupertagScore;
    private double[][] precomputedEstimates;
    private int n;

    public OutsideEstimator(WordWithSupertags[] sentence) {
        bestSupertagScore = new double[sentence.length];
        n = sentence.length;

        for (int i = 0; i < sentence.length; i++) {
            WordWithSupertags word = sentence[i];
            bestSupertagScore[i] = word.supertags.get(0).score;
        }

        // precompute outside estimates
        precomputedEstimates = new double[n+1][n+1];
        for( int start = 0; start <= n; start++) {
            for( int end = 0; end <= n; end++) {
                precomputedEstimates[start][end] = computeEstimate(start, end);
            }
        }
    }

    private double computeEstimate(int start, int end) {
        double ret = 0;

        for( int i = 0; i < start; i++ ) {
            ret += bestSupertagScore[i];
        }

        for( int i = end; i < n; i++ ) {
            ret += bestSupertagScore[i];
        }

        return ret;
    }


    public double estimate(int start, int end) {
        return precomputedEstimates[start][end];
    }

    public double estimate(Item item) {
        return estimate(item.getStart(), item.getEnd());
    }
}
