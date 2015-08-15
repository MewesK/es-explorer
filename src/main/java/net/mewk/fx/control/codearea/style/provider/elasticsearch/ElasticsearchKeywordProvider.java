package net.mewk.fx.control.codearea.style.provider.elasticsearch;

import com.google.common.collect.Lists;
import net.mewk.fx.control.codearea.style.StyleRange;
import net.mewk.fx.control.codearea.style.StyleRangesBuilder;
import net.mewk.fx.control.codearea.style.provider.StyleRangeProvider;
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

public class ElasticsearchKeywordProvider implements StyleRangeProvider {

    private static final List<String> queryFunctionNameList;
    private static final List<String> scoreFunctionNameList;
    private static final List<String> filterParserNameList;

    static {
        queryFunctionNameList = createStringListByType(QueryParser.class, QueryParser::names);
        scoreFunctionNameList = createStringListByType(ScoreFunctionParser.class, ScoreFunctionParser::getNames);
        filterParserNameList = createStringListByType(FilterParser.class, FilterParser::names);
    }

    @Override
    public StyleRangesBuilder compute(String text) {
        StyleRangesBuilder styleRangeBuilder = new StyleRangesBuilder();

        styleRangeBuilder.addAll(createStylesByStringList(
                text, "query-function", queryFunctionNameList));

        styleRangeBuilder.addAll(createStylesByStringList(
                text, "score-function", scoreFunctionNameList));

        styleRangeBuilder.addAll(createStylesByStringList(
                text, "filter-parser", filterParserNameList));

        return styleRangeBuilder;
    }

    /**
     * @param text
     * @param className
     * @param stringList
     * @return
     */
    private StyleRangesBuilder createStylesByStringList(String text, String className, List<String> stringList) {
        StyleRangesBuilder styleRangeBuilder = new StyleRangesBuilder();

        for (String string : stringList) {
            // Find occurrences of the given string
            Pattern pattern = Pattern.compile(Pattern.quote("\"" + string + "\""));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                // Create style span
                styleRangeBuilder.add(new StyleRange(matcher.start() + 1, matcher.end() - 1, className));
            }
        }

        return styleRangeBuilder;
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
