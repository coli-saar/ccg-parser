package de.saar.coli.ccgparser;


import java.util.Map;

public class SupertagWithScore {
    private static final Category
        N = Category.createAtomic("N"),
        S = Category.createAtomic("S"),
        NP = Category.createAtomic("NP");

    private static final Map<String,Category> CATEGORY_MAP = Map.of(
        "NP/N", Category.createForward(NP, N),
        "NP\\N", Category.createBackward(NP, N),
        "N/N", Category.createForward(N, N),
        "N", N,
        "S\\NP/N", Category.createForward(Category.createBackward(S, NP), N),
        "S\\NP", Category.createBackward(S, NP)
    );

    public String tag;
    public double score;
    private int positionInSupertagList;

    public int getPositionInSupertagList() {
        return positionInSupertagList;
    }

    public Category getCategory() {
        return CATEGORY_MAP.get(tag);
    }

    public void setPositionInSupertagList(int positionInSupertagList) {
        this.positionInSupertagList = positionInSupertagList;
    }
}
