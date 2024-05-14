package de.saar.coli.ccgparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Item {
    private int start, end;
    private Category category;
    private List<List<Item>> backpointers;
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

    public List<List<Item>> getBackpointers() {
        return backpointers;
    }

    public double getScore() {
        return score;
    }

    public String toString(OutsideEstimator estimator) {
        double estimate = estimator.estimate(this);
        return String.format("[%d-%d %s :%d+%d=%d]", start, end, category.toString(), (int) score, (int) estimate, (int) (score + estimate));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return start == item.start && end == item.end && Double.compare(score, item.score) == 0 && Objects.equals(category, item.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, category, score);
    }
}
