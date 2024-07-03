package de.saar.coli.ccgparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public void addBackpointer(Backpointer backpointer) {
        backpointers.add(backpointer);
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String toString(OutsideEstimator estimator) {
        double estimate = estimator.estimate(this);
        return String.format("[%d-%d %s :%d+%d=%d]", start, end, category.toString(), (int) score, (int) estimate, (int) (score + estimate));
    }

    @Override
    public String toString() {
        return String.format("[%d-%d %s]", start, end, category.toString());
    }

    /**
     * Caveat: Item#equals and Item#hashCode are somewhat hacky: they consider two items
     * as equal if the start position, end position, and category are the same.
     * The score is ignored - thus, two items that are the same except for their scores
     * are considered equal.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return start == item.start && end == item.end && Objects.equals(category, item.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, category);
    }
}
