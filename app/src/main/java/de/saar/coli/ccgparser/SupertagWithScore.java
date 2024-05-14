package de.saar.coli.ccgparser;

import jdk.jshell.spi.ExecutionControl;

public class SupertagWithScore {
    public String tag;
    public double score;
    private int positionInSupertagList;

    public int getPositionInSupertagList() {
        return positionInSupertagList;
    }

    public Category getCategory() {
        throw new UnsupportedOperationException();
    }

    public void setPositionInSupertagList(int positionInSupertagList) {
        this.positionInSupertagList = positionInSupertagList;
    }
}
