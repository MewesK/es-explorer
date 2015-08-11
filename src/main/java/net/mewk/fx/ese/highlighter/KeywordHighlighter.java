package net.mewk.fx.ese.highlighter;

import com.google.common.collect.Lists;
import net.mewk.fx.control.codearea.StyleSpanRange;
import net.mewk.fx.control.codearea.StyleSpanRangeBuilder;
import org.elasticsearch.index.query.FilterParser;
import org.elasticsearch.index.query.QueryParser;
import org.elasticsearch.index.query.functionscore.ScoreFunctionParser;
import org.reflections.Reflections;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KeywordHighlighter implements Highlighter {

    private static final List<String> queryFunctionNameList;
    private static final List<String> scoreFunctionNameList;
    private static final List<String> filterParserNameList;

    static {
        queryFunctionNameList = createStringListByType(QueryParser.class, QueryParser::names);
        scoreFunctionNameList = createStringListByType(ScoreFunctionParser.class, ScoreFunctionParser::getNames);
        filterParserNameList = createStringListByType(FilterParser.class, FilterParser::names);
    }

    @Override
    public StyleSpanRangeBuilder compute(String text) {
        StyleSpanRangeBuilder styleSpanRangeBuilder = new StyleSpanRangeBuilder();

        styleSpanRangeBuilder.addAll(createStylesByStringList(
                text, "query-function", queryFunctionNameList));

        styleSpanRangeBuilder.addAll(createStylesByStringList(
                text, "score-function", scoreFunctionNameList));

        styleSpanRangeBuilder.addAll(createStylesByStringList(
                text, "filter-parser", filterParserNameList));

        return styleSpanRangeBuilder;
    }

    /**
     * @param text
     * @param className
     * @param stringList
     * @return
     */
    private StyleSpanRangeBuilder createStylesByStringList(String text, String className, List<String> stringList) {
        StyleSpanRangeBuilder styleSpanRangeBuilder = new StyleSpanRangeBuilder();

        for (String string : stringList) {
            // Find occurrences of the given string
            Pattern pattern = Pattern.compile(Pattern.quote("\"" + string + "\""));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                // Create style span
                styleSpanRangeBuilder.add(new StyleSpanRange(matcher.start() + 1, matcher.end() - 1, className));
            }
        }

        return styleSpanRangeBuilder;
    }

    /**
     * Returns String list based on a given type and a given generator function.
     *
     * @param type              The class of type
     * @param generatorFunction A function that returns a String array based on the instance of type
     * @param <T>               The type
     * @return Name list
     */
    private static <T> List<String> createStringListByType(Class<T> type, Function<T, String[]> generatorFunction) {
        List<String> nameList = Lists.newArrayList();

        // Limit scan to package of parent class
        Reflections reflections = new Reflections(type.getPackage().getName());

        // Scan for sub types
        Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(type);
        subTypes.add(type);

        for (Class<? extends T> subType : subTypes) {
            try {
                // Create sub type instance
                T instance = subType.newInstance();

                // Get names
                List<String> innerNameList = Lists.newArrayList(generatorFunction.apply(instance));

                // Remove camel-cased alternative names
                innerNameList = innerNameList.stream().filter(
                        queryParserName -> queryParserName != null &&
                                queryParserName.toLowerCase().equals(queryParserName)
                ).collect(Collectors.<String>toList());

                nameList.addAll(innerNameList);
            } catch (InstantiationException | IllegalAccessException ignored) {
            }
        }

        // Sort names
        nameList.sort(Comparator.<String>naturalOrder());

        return nameList;
    }
}
