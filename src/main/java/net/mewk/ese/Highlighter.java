package net.mewk.ese;

import com.google.common.collect.Lists;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Highlighter {

    private static final List<HighlightPattern> SUB_PATTERNS = Lists.newArrayList();
    private static final Pattern PATTERN;

    static {
        // Define sub patterns
        SUB_PATTERNS.add(new HighlightPattern("\"([^\"\\\\]|\\\\.)*\"\\s*:", "KEY", "key"));
        SUB_PATTERNS.add(new HighlightPattern("\\{|\\}", "BRACE", "brace"));
        SUB_PATTERNS.add(new HighlightPattern("\\[|\\]", "BRACKET", "bracket"));
        SUB_PATTERNS.add(new HighlightPattern("\\,", "COMMA", "comma"));

        SUB_PATTERNS.add(new HighlightPattern("\"([^\"\\\\]|\\\\.)*\"", "STRING", "string"));
        SUB_PATTERNS.add(new HighlightPattern("(?i)(true|false)", "BOOL", "bool"));
        SUB_PATTERNS.add(new HighlightPattern("(?i)(null)", "NULL", "null"));
        SUB_PATTERNS.add(new HighlightPattern("(\\d+)", "INT", "int"));
        SUB_PATTERNS.add(new HighlightPattern("(\\d*\\.\\d+)", "DOUBLE", "double"));

        // Build main pattern
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (HighlightPattern highlightPattern : SUB_PATTERNS) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append("|");
            }
            stringBuilder.append("(?<").append(highlightPattern.getGroupName()).append(">").
                    append(highlightPattern.getPattern()).append(")");
        }

        PATTERN = Pattern.compile(stringBuilder.toString());
    }

    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        Matcher matcher = PATTERN.matcher(text);
        int lastMatchEnd = 0;
        while (matcher.find()) {
            // Create className list
            List<String> styleClassList = Lists.newArrayList();
            styleClassList.add("match");
            for (HighlightPattern highlightPattern : SUB_PATTERNS) {
                if (matcher.group(highlightPattern.getGroupName()) != null) {
                    styleClassList.add(highlightPattern.getClassName());
                    break;
                }
            }

            // Add dummy style between end of last match and beginning of current match
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastMatchEnd);

            // Add styles to the current match
            spansBuilder.add(styleClassList, matcher.end() - matcher.start());

            // Remember end of current match
            lastMatchEnd = matcher.end();
        }
        // Add dummy style between end of last match and end of text
        spansBuilder.add(Collections.emptyList(), text.length() - lastMatchEnd);

        return spansBuilder.create();
    }
}
