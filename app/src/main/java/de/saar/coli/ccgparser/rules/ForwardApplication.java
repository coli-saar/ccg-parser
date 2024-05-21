package de.saar.coli.ccgparser.rules;

import de.saar.coli.ccgparser.Category;
import de.saar.coli.ccgparser.Item;

public class ForwardApplication extends CombinatoryRule {
    @Override
    public boolean isForward() {
        return true;
    }

    @Override
    public String getSymbol() {
        return ">";
    }

    @Override
    public Item combine(Item functor, Item argument) {
        Category functorCat = functor.getCategory();
        if( functorCat.isFunctional() && functorCat.getArgument().equals(argument.getCategory())) {
            return create(functor.getStart(), argument.getEnd(), functorCat.getFunctor(), functor, argument, this);
        } else {
            return null;
        }
    }
}
