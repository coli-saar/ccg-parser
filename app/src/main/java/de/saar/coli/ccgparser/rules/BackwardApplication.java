package de.saar.coli.ccgparser.rules;

import de.saar.coli.ccgparser.Category;
import de.saar.coli.ccgparser.Item;

public class BackwardApplication extends CombinatoryRule {
    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public String getSymbol() {
        return "<";
    }

    @Override
    public Item combine(Item functor, Item argument) {
        Category functorCat = functor.getCategory();
        if( functorCat.isFunctional() && functorCat.getType() == Category.CategoryType.BACKWARD && functorCat.getArgument().equals(argument.getCategory())) {
            return create(argument.getStart(), functor.getEnd(), functorCat.getFunctor(), functor, argument, this);
        } else {
            return null;
        }
    }
}
