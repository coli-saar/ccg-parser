package de.saar.coli.ccgparser;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestCategory {
    @Test
    public void testAtomic() {
        Category category = Category.createAtomic("NP");
        assertEquals(Category.CategoryType.ATOMIC, category.getType());
        assertEquals("NP", category.toString());
    }

    @Test
    public void testForward() {
        Category category = Category.createForward(Category.createAtomic("S"), Category.createAtomic("NP"));
        assertEquals(Category.CategoryType.FORWARD, category.getType());
        assertEquals("(S/NP)", category.toString());
    }

    @Test
    public void testBackward() {
        Category category = Category.createBackward(Category.createAtomic("S"), Category.createAtomic("NP"));
        assertEquals(Category.CategoryType.BACKWARD, category.getType());
        assertEquals("(S\\NP)", category.toString());
        System.out.println(category);
    }
}
