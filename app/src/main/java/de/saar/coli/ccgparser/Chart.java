package de.saar.coli.ccgparser;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Chart {
    private Set<Item>[][] chart; // chart[start][end]
    private int n;

    public Chart(int n) {
        this.n = n;
        chart = new Set[n+1][n+1];

        for( int i=0; i<n+1; i++ ) {
            for( int j=0; j<n+1; j++ ) {
                chart[i][j] = new HashSet<>();
            }
        }
    }

    public Set<Item> get(int start, int end) {
        return chart[start][end];
    }

    public void add(Item item) {
        // TODO - this is too simple
        chart[item.getStart()][item.getEnd()].add(item);
    }

    /**
     * Returns an Iterable over all items that end at the given position.
     *
     * @param end
     * @return
     */
    public Iterable<Item> getItemsWithEnd(int end) {
        List<Set<Item>> iterables = new ArrayList<>();
        for( int i = 0; i < end; i++ ) {
            iterables.add(chart[i][end]);
        }
        return Iterables.concat(iterables);
    }

    /**
     * Returns an Iterable over all items that start at the given position.
     *
     * @param start
     * @return
     */
    public Iterable<Item> getItemsWithStart(int start) {
        List<Set<Item>> iterables = new ArrayList<>();
        for( int i = start+1; i <= n; i++ ) {
            iterables.add(chart[start][i]);
        }
        return Iterables.concat(iterables);
    }
}
