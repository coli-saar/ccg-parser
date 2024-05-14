package de.saar.coli.ccgparser.categoryparser;

import de.saar.coli.ccgparser.Category;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class CatParser {
    public static final CatParser CATEGORY_PARSER = new CatParser();

    public Category parse(String cat) {
        ANTLRInputStream inputStream = new ANTLRInputStream(cat);
        CategoryLexer lexer = new CategoryLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CategoryParser catparser = new CategoryParser(tokens);

        return visitCategory(catparser.category());
    }

    private Category visitCategory(CategoryParser.CategoryContext category) {
        if( category.ATOMIC() == null ) {
            Category functor = visitSubcategory(category.functional_category().subcategory(0));
            Category argument = visitSubcategory(category.functional_category().subcategory(1));

            if( category.functional_category().FORWARD() != null ) {
                return Category.createForward(functor, argument);
            } else {
                return Category.createBackward(functor, argument);
            }
        } else {
            return Category.createAtomic(category.ATOMIC().getText());
        }
    }

    private Category visitSubcategory(CategoryParser.SubcategoryContext subcategory) {
        if( subcategory.ATOMIC() == null ) {
            Category functor = visitSubcategory(subcategory.functional_category().subcategory(0));
            Category argument = visitSubcategory(subcategory.functional_category().subcategory(1));

            if( subcategory.functional_category().FORWARD() != null ) {
                return Category.createForward(functor, argument);
            } else {
                return Category.createBackward(functor, argument);
            }
        } else {
            return Category.createAtomic(subcategory.ATOMIC().getText());
        }
    }
}
