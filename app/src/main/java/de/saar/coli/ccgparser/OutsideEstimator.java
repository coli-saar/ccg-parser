package de.saar.coli.ccgparser;

import java.util.List;

public class OutsideEstimator {
    public OutsideEstimator(WordWithSupertags[] sentence) {

    }

    public double estimate(int start, int end) {
        return 0;
    }

    public double estimate(Item item) {
        return estimate(item.getStart(), item.getEnd());
    }
}
