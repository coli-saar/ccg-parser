package de.saar.coli.ccgparser;

import java.util.List;

public class Backpointer {
    private List<Item> pieces;
    private CombinatoryRule combinatoryRule;

    public Backpointer(List<Item> pieces, CombinatoryRule combinatoryRule) {
        this.pieces = pieces;
        this.combinatoryRule = combinatoryRule;
    }

    public List<Item> getPieces() {
        return pieces;
    }

    public CombinatoryRule getCombinatoryRule() {
        return combinatoryRule;
    }
}
