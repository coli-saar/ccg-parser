package de.saar.coli.ccgparser.rules;

import de.saar.coli.ccgparser.Backpointer;
import de.saar.coli.ccgparser.Category;
import de.saar.coli.ccgparser.Item;

import java.util.List;

public abstract class CombinatoryRule {
//    FORWARD_APPLICATION(">", true),
//    BACKWARD_APPLICATION("<", false),
//    TYPECHANGE("TC", false)
//    ;
//
//    private String symbol;
//    private boolean isForward;
//
//    CombinatoryRule(String symbol, boolean isForward) {
//        this.symbol = symbol;
//        this.isForward = isForward;
//    }
//
//    public String getSymbol() {
//        return symbol;
//    }
//
    public abstract boolean isForward();
    public abstract String getSymbol();

    public abstract Item combine(Item functor, Item argument);

    protected Item create(int start, int end, Category category, Item functor, Item argument, CombinatoryRule combinatoryRule) {
        Item resultItem = new Item(start, end, category, functor.getScore() + argument.getScore());
        resultItem.addBackpointer(new Backpointer(List.of(functor, argument), combinatoryRule));
        return resultItem;
    }
}
