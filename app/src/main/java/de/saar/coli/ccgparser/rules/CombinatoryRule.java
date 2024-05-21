package de.saar.coli.ccgparser.rules;

import de.saar.coli.ccgparser.Backpointer;
import de.saar.coli.ccgparser.Category;
import de.saar.coli.ccgparser.Item;

import java.util.List;

// TODO - implement unit tests for everything
public abstract class CombinatoryRule {
    public abstract boolean isForward();
    public abstract String getSymbol();
    public abstract Item combine(Item functor, Item argument);

    protected Item create(int start, int end, Category category, Item functor, Item argument, CombinatoryRule combinatoryRule) {
        Item resultItem = new Item(start, end, category, functor.getScore() + argument.getScore());
        resultItem.addBackpointer(new Backpointer(List.of(functor, argument), combinatoryRule));
        return resultItem;
    }

}
