package de.saar.coli.ccgparser;

import java.util.List;

public class WordWithSupertags {
    public String word;
    public List<SupertagWithScore> supertags;

    @Override
    public String toString() {
        return word;
    }
}