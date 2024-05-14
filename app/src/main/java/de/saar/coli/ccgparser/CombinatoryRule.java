package de.saar.coli.ccgparser;

public enum CombinatoryRule {
    FORWARD_APPLICATION(">", true),
    BACKWARD_APPLICATION("<", false),
    TYPECHANGE("TC", false)
    ;

    private String symbol;
    private boolean isForward;

    CombinatoryRule(String symbol, boolean isForward) {
        this.symbol = symbol;
        this.isForward = isForward;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isForward() {
        return isForward;
    }
}
