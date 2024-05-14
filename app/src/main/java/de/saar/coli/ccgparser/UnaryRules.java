package de.saar.coli.ccgparser;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.saar.coli.ccgparser.categoryparser.CatParser.CATEGORY_PARSER;

public class UnaryRules {
    private ListMultimap<Category, Category> unaryRules = ArrayListMultimap.create();

    private static final String regex = "(\\S+)\\s+(\\S+)";
    private static final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

    public List<Category> get(Category src) {
        return unaryRules.get(src);
    }

    public static UnaryRules load(File file) throws IOException {
        UnaryRules ret = new UnaryRules();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;

        while( (line = reader.readLine() ) != null ) {
            if( ! line.startsWith("#") ) {
                line = line.trim();
                if( ! line.isEmpty() ) {
                    Matcher matcher = pattern.matcher(line);
                    if( matcher.matches()) {
                        Category src = CATEGORY_PARSER.parse(matcher.group(1));
                        Category tgt = CATEGORY_PARSER.parse(matcher.group(2));
                        ret.unaryRules.put(src, tgt);
                    }
                }
            }
        }

        return ret;
    }
}
