package de.saar.coli.ccgparser;

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

    public String getAtomic() {
        return atomic;
    }

    public Category getFunctor() {
        return functor;
    }

    public Category getArgument() {
        return argument;
    }
}
