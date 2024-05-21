package de.saar.coli.ccgparser;

import java.util.Objects;

public class Category {
    public enum CategoryType {
        ATOMIC,
        FORWARD,
        BACKWARD
    }

    private CategoryType type;
    private String atomic;
    private Category functor, argument;

    public static Category createAtomic(String atomic) {
        Category cat = new Category();
        cat.type = CategoryType.ATOMIC;
        cat.atomic = atomic;
        cat.functor = null;
        cat.argument = null;
        return cat;
    }

    public static Category createForward(Category functor, Category argument) {
        Category cat = new Category();
        cat.type = CategoryType.FORWARD;
        cat.functor = functor;
        cat.argument = argument;
        cat.atomic = null;
        return cat;
    }

    public static Category createBackward(Category functor, Category argument) {
        Category cat = new Category();
        cat.type = CategoryType.BACKWARD;
        cat.functor = functor;
        cat.argument = argument;
        cat.atomic = null;
        return cat;
    }

    @Override
    public String toString() {
        switch(type) {
            case ATOMIC: return atomic;
            case FORWARD: return String.format("(%s/%s)", functor.toString(), argument.toString());
            case BACKWARD: return String.format("(%s\\%s)", functor.toString(), argument.toString());
        }

        return null;
    }

    public CategoryType getType() {
        return type;
    }

    public boolean isFunctional() {
        return type != CategoryType.ATOMIC;
    }

    public String getAtomic() {
        return atomic;
    }

    public Category getFunctor() {
        return functor;
    }

    public Category getArgument() {
        return argument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return type == category.type && Objects.equals(atomic, category.atomic) && Objects.equals(functor, category.functor) && Objects.equals(argument, category.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, atomic, functor, argument);
    }
}
