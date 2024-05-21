package de.saar.coli.ccgparser.rules;

import de.saar.coli.ccgparser.Category;
import de.saar.coli.ccgparser.Item;

public class BackwardCrossedComposition extends CombinatoryRule {
    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public String getSymbol() {
        return "<Bx";
    }

    @Override
    public Item combine(Item functor, Item argument) {
        Category functorCat = functor.getCategory();
        Category argumentCat = argument.getCategory();

        if( functorCat.isFunctional() && argumentCat.isFunctional() && functorCat.getType() != argumentCat.getType() && functorCat.getArgument().equals(argumentCat.getFunctor()) ) {
            return create(argument.getStart(), functor.getEnd(), functorCat.compose(argumentCat), functor, argument, this);
        } else {
            return null;
        }
    }
}
