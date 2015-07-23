package net.mewk.ese.highlighter;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlighter implements Highlighter {

    private static final List<SyntaxHighlightPattern> SUB_PATTERNS = Lists.newArrayList();
    private static final Pattern PATTERN;

    static {
        // Define sub patterns
        SUB_PATTERNS.add(new SyntaxHighlightPattern("\"([^\"\\\\]|\\\\.)*\"\\s*:", "KEY", "key"));
        SUB_PATTERNS.add(new SyntaxHighlightPattern("\\{|\\}", "BRACE", "brace"));
        SUB_PATTERNS.add(new SyntaxHighlightPattern("\\[|\\]", "BRACKET", "bracket"));
        SUB_PATTERNS.add(new SyntaxHighlightPattern("\\,", "COMMA", "comma"));

        SUB_PATTERNS.add(new SyntaxHighlightPattern("\"([^\"\\\\]|\\\\.)*\"", "STRING", "string"));
        SUB_PATTERNS.add(new SyntaxHighlightPattern("(?i)(true|false)", "BOOL", "bool"));
        SUB_PATTERNS.add(new SyntaxHighlightPattern("(?i)(null)", "NULL", "null"));
        SUB_PATTERNS.add(new SyntaxHighlightPattern("(\\d+)", "INT", "int"));
        SUB_PATTERNS.add(new SyntaxHighlightPattern("(\\d*\\.\\d+)", "DOUBLE", "double"));

        // Build main pattern
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (SyntaxHighlightPattern syntaxHighlightPattern : SUB_PATTERNS) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append("|");
            }
            stringBuilder.append("(?<").append(syntaxHighlightPattern.getGroupName()).append(">").
                    append(syntaxHighlightPattern.getPattern()).append(")");
        }

        PATTERN = Pattern.compile(stringBuilder.toString());
    }

    public List<Span> compute(String text) {
        List<Span> spanList = Lists.newArrayList();

        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            // Create className list
            List<String> classNameList = Lists.newArrayList();
            for (SyntaxHighlightPattern syntaxHighlightPattern : SUB_PATTERNS) {
                if (matcher.group(syntaxHighlightPattern.getGroupName()) != null) {
                    classNameList.add(syntaxHighlightPattern.getClassName());
                    break;
                }
            }
            // Create span
            spanList.add(new Span(matcher.start(), matcher.end(), classNameList));
        }

        return spanList;
    }
}
