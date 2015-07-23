package net.mewk.ese.highlighter;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class Span implements Comparable<Span>, Serializable {
    private int start;
    private int end;
    private List<String> classNameList = Lists.newArrayList();

    public Span() {
        this(0, 0);
    }

    public Span(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public Span(int start, int end, String className) {
        this(start, end);
        classNameList.add(className);
    }

    public Span(int start, int end, List<String> classNameList) {
        this(start, end);
        this.classNameList = classNameList;
    }

    public int length() {
        return end - start;
    }

    public boolean overlaps(Span span) {
        return span != null && (
                contains(span.getStart()) || contains(span.getEnd()) ||
                        span.contains(start) || span.contains(end)
        );
    }

    public List<Span> stamp(Span span) {
        final List<Span> spanList;

        if (!overlaps(span)) {
            spanList = start < span.getStart() ?
                    Lists.newArrayList(this, span) :
                    Lists.newArrayList(span, this);
        } else {
            final List<String> mergedClassNameList = Lists.newArrayList();
            mergedClassNameList.addAll(classNameList);
            mergedClassNameList.addAll(span.getClassNameList());

            final Span first = start < span.getStart() ?
                    new Span(start, span.getStart(), classNameList) :
                    new Span(span.getStart(), start, span.getClassNameList());

            final Span last = end > span.getEnd() ?
                    new Span(span.getEnd(), end, classNameList) :
                    new Span(end, span.getEnd(), span.getClassNameList());

            spanList = Lists.newArrayList();

            if (!first.isEmpty()) {
                spanList.add(first);
            }

            spanList.add(new Span(first.getEnd(), last.getStart(), mergedClassNameList));

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

    @Override
    public int compareTo(Span span) {
        return span == null ? 1 : Integer.compare(start, span.getStart());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Span) {
            final Span span = (Span) object;
            return start == span.getStart() && end == span.getEnd();
        }
        return object.equals(this);
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
