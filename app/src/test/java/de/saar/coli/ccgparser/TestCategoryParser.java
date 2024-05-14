package de.saar.coli.ccgparser;

import de.saar.coli.ccgparser.categoryparser.CatParser;
import org.junit.Assert;
import org.junit.Test;

public class TestCategoryParser {
    @Test
    public void testParse1() {
        CatParser cp = new CatParser();
        Category cat = cp.parse("((S[b]\\NP)/PP)/NP");
        Assert.assertEquals("(((S\\NP)/PP)/NP)", cat.toString());
    }

    @Test
    public void testParse2() {
        CatParser cp = new CatParser();
        Category cat = cp.parse("(S[dcl]\\NP)/(S[b]\\NP)");
        Assert.assertEquals("((S\\NP)/(S\\NP))", cat.toString());
    }

    @Test
    public void testParseAtomic() {
        CatParser cp = new CatParser();
        Category cat = cp.parse("S[dcl]");
        Assert.assertEquals("S", cat.toString());
    }
}
