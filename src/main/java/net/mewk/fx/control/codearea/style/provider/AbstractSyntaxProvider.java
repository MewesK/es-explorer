package net.mewk.fx.control.codearea.style.provider;

import com.google.common.collect.Lists;
import net.mewk.fx.control.codearea.style.StyleRange;
import net.mewk.fx.control.codearea.style.StyleRangesBuilder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractSyntaxProvider implements StyleRangeProvider {

    protected static final List<SyntaxPattern> SUB_PATTERNS = Lists.newArrayList();
    protected static Pattern PATTERN;

    protected static void buildPattern() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for (SyntaxPattern syntaxPattern : SUB_PATTERNS) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append("|");
            }
            stringBuilder.append("(?<").append(syntaxPattern.getGroupName()).append(">").
                    append(syntaxPattern.getPattern()).append(")");
        }

        PATTERN = Pattern.compile(stringBuilder.toString());
    }

    public StyleRangesBuilder compute(String text) {
        StyleRangesBuilder styleRangesBuilder = new StyleRangesBuilder();

        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            // Create className list
            List<String> classNameList = Lists.newArrayList();
            for (SyntaxPattern syntaxPattern : SUB_PATTERNS) {
                if (matcher.group(syntaxPattern.getGroupName()) != null) {
                    classNameList.add(syntaxPattern.getClassName());
                    break;
                }
            }
            // Create span
            styleRangesBuilder.add(new StyleRange(matcher.start(), matcher.end(), classNameList));
        }

        return styleRangesBuilder;
    }

    protected static class SyntaxPattern {
        private final String pattern;
        private final String groupName;
        private final String className;

        public SyntaxPattern(String pattern, String groupName, String className) {
            this.pattern = pattern;
            this.groupName = groupName;
            this.className = className;
        }

        public String getPattern() {
            return pattern;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getClassName() {
            return className;
        }
    }
}
