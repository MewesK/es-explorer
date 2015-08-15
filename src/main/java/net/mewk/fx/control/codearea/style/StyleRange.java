package net.mewk.fx.control.codearea.style;

import com.google.common.collect.Lists;
import org.fxmisc.richtext.StyleSpan;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class StyleRange implements Comparable<StyleRange>, Serializable {

    private int start;
    private int end;
    private List<String> classNameList = Lists.newArrayList();

    public StyleRange() {
        this(0, 0);
    }

    public StyleRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public StyleRange(int start, int end, String className) {
        this(start, end);
        classNameList.add(className);
    }

    public StyleRange(int start, int end, List<String> classNameList) {
        this(start, end);
        this.classNameList = classNameList;
    }

    public int length() {
        return Math.abs(end - start);
    }

    public boolean overlaps(StyleRange span) {
        return span != null && (
                equals(span) || contains(span.getStart()) || contains(span.getEnd()) ||
                        span.contains(start) || span.contains(end)
        );
    }

    public boolean touches(StyleRange span) {
        return span != null && (
                touches(span.getStart()) || touches(span.getEnd()) ||
                        span.touches(start) || span.touches(end)
        );
    }

    public List<StyleRange> overlay(StyleRange span) {
        final List<StyleRange> spanList;

        if (!touches(span)) {
            spanList = start < span.getStart() ?
                    Lists.newArrayList(this, span) :
                    Lists.newArrayList(span, this);
        } else {

            final StyleRange first = start < span.getStart() ?
                    new StyleRange(start, span.getStart(), classNameList) :
                    new StyleRange(span.getStart(), start, span.getClassNameList());

            final StyleRange last = end > span.getEnd() ?
                    new StyleRange(span.getEnd(), end, classNameList) :
                    new StyleRange(end, span.getEnd(), span.getClassNameList());

            final List<String> mergedClassNameList = Lists.newArrayList();
            mergedClassNameList.addAll(classNameList);
            mergedClassNameList.addAll(span.getClassNameList());

            final StyleRange middle = new StyleRange(first.getEnd(), last.getStart(), mergedClassNameList);

            spanList = Lists.newArrayList();

            if (!first.isEmpty()) {
                spanList.add(first);
            }

            if (!middle.isEmpty()) {
                spanList.add(middle);
            }

            if (!last.isEmpty()) {
                spanList.add(last);
            }
        }

        return spanList;
    }

    public boolean contains(int value) {
        return value > start && value < end;
    }

    public boolean touches(int value) {
        return value >= start && value <= end;
    }

    public boolean isEmpty() {
        return end - start == 0;
    }

    public StyleSpan<Collection<String>> createStyleSpan() {
        return new StyleSpan<>(classNameList, length());
    }

    @Override
    public int compareTo(@NotNull StyleRange span) {
        return Integer.compare(start, span.getStart());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof StyleRange) {
            final StyleRange span = (StyleRange) object;
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
