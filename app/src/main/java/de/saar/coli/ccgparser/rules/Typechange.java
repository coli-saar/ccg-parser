package de.saar.coli.ccgparser.rules;

import de.saar.coli.ccgparser.Item;

public class Typechange extends CombinatoryRule {
    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public String getSymbol() {
        return "TC";
    }

    @Override
    public Item combine(Item functor, Item argument) {
        return null;
    }
}
