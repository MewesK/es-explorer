package net.mewk.ese.highlighter;

public class SyntaxHighlightPattern {
    private final String pattern;
    private final String groupName;
    private final String className;

    public SyntaxHighlightPattern(String pattern, String groupName, String className) {
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
