package de.saar.coli.ccgparser;

import java.util.HashSet;
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
}
