package de.saar.coli.ccgparser.rules;

import de.saar.coli.ccgparser.Backpointer;
import de.saar.coli.ccgparser.Category;
import de.saar.coli.ccgparser.Item;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CombinatoryRule that = (CombinatoryRule) o;
        return Objects.equals(getSymbol(), that.getSymbol());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getSymbol());
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}
