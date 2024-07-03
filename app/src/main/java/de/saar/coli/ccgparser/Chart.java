package de.saar.coli.ccgparser;

import com.google.common.collect.Iterables;

import java.util.*;

public class Chart {
    private Map<Item,Item>[][] chart; // chart[start][end]
    private int n;

    public Chart(int n) {
        this.n = n;
        chart = new Map[n+1][n+1];

        for( int i=0; i<n+1; i++ ) {
            for( int j=0; j<n+1; j++ ) {
                chart[i][j] = new HashMap<>();
            }
        }
    }

    public Set<Item> get(int start, int end) {
        return chart[start][end].keySet();
    }

    public void add(Item item) {
        // TODO - this is too simple
        chart[item.getStart()][item.getEnd()].put(item, item);
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
            iterables.add(chart[i][end].keySet());
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
            iterables.add(chart[start][i].keySet());
        }
        return Iterables.concat(iterables);
    }

    public boolean contains(Item item) {
        return chart[item.getStart()][item.getEnd()].containsKey(item);
    }

    public Item getCanonicalItem(Item item) {
        return chart[item.getStart()][item.getEnd()].get(item);
    }

    public void updateCanonicalItem(Item item) {
        chart[item.getStart()][item.getEnd()].remove(item);
        chart[item.getStart()][item.getEnd()].put(item, item);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        for( int i=0; i<n+1; i++ ) {
            for( int j=0; j<n+1; j++ ) {
                if( ! chart[i][j].isEmpty() ) {
                    ret.append(String.format("[%d-%d] ", i, j));
                    ret.append(chart[i][j].keySet());
                    ret.append("\n");
                }
            }
        }

        return ret.toString();
    }
}
