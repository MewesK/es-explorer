package net.mewk.ese;

public class HighlightPattern {
    private String pattern;
    private String groupName;
    private String className;

    public HighlightPattern(String pattern, String groupName, String className) {
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
