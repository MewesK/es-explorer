package net.mewk.richtext;

import com.google.common.collect.Lists;
import org.fxmisc.richtext.StyleSpan;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class StyleSpanRange implements Comparable<StyleSpanRange>, Serializable {

    private int start;
    private int end;
    private List<String> classNameList = Lists.newArrayList();

    public StyleSpanRange() {
        this(0, 0);
    }

    public StyleSpanRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public StyleSpanRange(int start, int end, String className) {
        this(start, end);
        classNameList.add(className);
    }

    public StyleSpanRange(int start, int end, List<String> classNameList) {
        this(start, end);
        this.classNameList = classNameList;
    }

    public int length() {
        return Math.abs(end - start);
    }

    public boolean overlaps(StyleSpanRange span) {
        return span != null && (
                contains(span.getStart()) || contains(span.getEnd()) ||
                        span.contains(start) || span.contains(end)
        );
    }

    public List<StyleSpanRange> overlay(StyleSpanRange span) {
        final List<StyleSpanRange> spanList;

        if (!overlaps(span)) {
            spanList = start < span.getStart() ?
                    Lists.newArrayList(this, span) :
                    Lists.newArrayList(span, this);
        } else {
            final List<String> mergedClassNameList = Lists.newArrayList();
            mergedClassNameList.addAll(classNameList);
            mergedClassNameList.addAll(span.getClassNameList());

            final StyleSpanRange first = start < span.getStart() ?
                    new StyleSpanRange(start, span.getStart(), classNameList) :
                    new StyleSpanRange(span.getStart(), start, span.getClassNameList());

            final StyleSpanRange last = end > span.getEnd() ?
                    new StyleSpanRange(span.getEnd(), end, classNameList) :
                    new StyleSpanRange(end, span.getEnd(), span.getClassNameList());

            spanList = Lists.newArrayList();

            if (!first.isEmpty()) {
                spanList.add(first);
            }

            spanList.add(new StyleSpanRange(first.getEnd(), last.getStart(), mergedClassNameList));

            if (!last.isEmpty()) {
                spanList.add(last);
            }
        }

        return spanList;
    }

    public boolean contains(int value) {
        return value >= start && value <= end;
    }

    public boolean isEmpty() {
        return end - start == 0;
    }

    public StyleSpan<Collection<String>> createStyleSpan() {
        return new StyleSpan<>(classNameList, length());
    }

    @Override
    public int compareTo(@NotNull StyleSpanRange span) {
        return Integer.compare(start, span.getStart());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof StyleSpanRange) {
            final StyleSpanRange span = (StyleSpanRange) object;
            return start == span.getStart() && end == span.getEnd();
        }
        return object != null && object.equals(this);
    }

    @Override
    public String toString() {
        return start + ".." + end + " " + classNameList;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<String> getClassNameList() {
        return classNameList;
    }

    public void setClassNameList(List<String> classNameList) {
        this.classNameList = classNameList;
    }
}
