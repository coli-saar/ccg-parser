package de.saar.coli.ccgparser;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private int start, end;
    private Category category;
    private List<Backpointer> backpointers;
    private double score;

    public Item(int start, int end, Category category, double score) {
        this.start = start;
        this.end = end;
        this.category = category;
        this.score = score;
        backpointers = new ArrayList<>();
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Category getCategory() {
        return category;
    }

    public List<Backpointer> getBackpointers() {
        return backpointers;
    }

    public double getScore() {
        return score;
    }
}
