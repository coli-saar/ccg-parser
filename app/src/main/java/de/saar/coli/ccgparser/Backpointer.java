package de.saar.coli.ccgparser;

import de.saar.coli.ccgparser.rules.CombinatoryRule;

import java.util.List;

public class Backpointer {
    private List<Item> pieces;
    private CombinatoryRule combinatoryRule;
    private String annotation;

    public Backpointer(List<Item> pieces, CombinatoryRule combinatoryRule) {
        this.pieces = pieces;
        this.combinatoryRule = combinatoryRule;
        annotation = null;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public List<Item> getPieces() {
        return pieces;
    }

    public CombinatoryRule getCombinatoryRule() {
        return combinatoryRule;
    }
}
