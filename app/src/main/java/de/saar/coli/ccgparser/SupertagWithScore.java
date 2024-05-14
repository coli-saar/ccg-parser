package de.saar.coli.ccgparser;

import static de.saar.coli.ccgparser.categoryparser.CatParser.CATEGORY_PARSER;

public class SupertagWithScore {
    public String tag;
    public double score;
    private int positionInSupertagList;

    public int getPositionInSupertagList() {
        return positionInSupertagList;
    }

    public Category getCategory() {
        return CATEGORY_PARSER.parse(tag);
    }

    public void setPositionInSupertagList(int positionInSupertagList) {
        this.positionInSupertagList = positionInSupertagList;
    }
}
