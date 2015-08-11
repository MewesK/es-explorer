package net.mewk.fx.control.codearea;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StyleSpanRangeBuilder extends ArrayList<StyleSpanRange> {

    public StyleSpans<Collection<String>> create() {
        final StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();

        if (isEmpty()) {
            // Add blank span
            styleSpansBuilder.add(new StyleSpan<>(Lists.newArrayList(), 0));
        } else {
            // Overlay
            List<StyleSpanRange> styleSpanRangeList = overlay(this);

            // Merge
            styleSpanRangeList = merge(styleSpanRangeList);

            // Sort
            styleSpanRangeList.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

            // Convert
            StyleSpanRange lastSpan = new StyleSpanRange();
            for (StyleSpanRange span : styleSpanRangeList) {
                // Add blank span if necessary
                if (lastSpan.getEnd() < span.getStart()) {
                    StyleSpanRange blankSpan = new StyleSpanRange(lastSpan.getEnd(), span.getStart());
                    styleSpansBuilder.add(blankSpan.createStyleSpan());
                }

                styleSpansBuilder.add(span.createStyleSpan());
                lastSpan = span;
            }
        }

        // Finalize
        return styleSpansBuilder.create();
    }

    private List<StyleSpanRange> overlay(List<StyleSpanRange> styleSpanRangeList) {
        List<StyleSpanRange> newStyleSpanRangeList = Lists.newArrayList();

        // Sort
        styleSpanRangeList.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

        boolean changed = false;
        for (StyleSpanRange span : styleSpanRangeList) {
            if (newStyleSpanRangeList.size() != 0) {
                int lastIndex = newStyleSpanRangeList.size() - 1;
                StyleSpanRange topSpan = newStyleSpanRangeList.get(lastIndex);
                if (topSpan.overlaps(span)) {
                    changed = true;

                    // Overlay operation
                    newStyleSpanRangeList.remove(lastIndex);
                    newStyleSpanRangeList.addAll(topSpan.overlay(span));
                    continue;
                }
            }

            newStyleSpanRangeList.add(span);
        }

        // Repeat if changed
        if (changed) {
            newStyleSpanRangeList = overlay(newStyleSpanRangeList);
        }

        return newStyleSpanRangeList;
    }

    private List<StyleSpanRange> merge(List<StyleSpanRange> styleSpanRangeList) {
        List<StyleSpanRange> newStyleSpanRangeList = Lists.newArrayList();

        // Sort
        styleSpanRangeList.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

        boolean changed = false;
        int size = styleSpanRangeList.size();
        for (int i = 0; i < size; i++) {
            if (i + 1 < size) {
                StyleSpanRange current = styleSpanRangeList.get(i);
                StyleSpanRange next = styleSpanRangeList.get(i + 1);

                // Overlap check
                if (next.contains(current.getEnd())) {
                    Collection<String> commonClasses = CollectionUtils.retainAll(current.getClassNameList(), next.getClassNameList());

                    // Class names check
                    if (commonClasses.size() == current.getClassNameList().size() && commonClasses.size() == next.getClassNameList().size()) {
                        changed = true;

                        // Merge operation
                        next.setStart(current.getStart());
                        current = null;
                    }
                }

                if (current != null) {
                    newStyleSpanRangeList.add(current);
                }
            }
        }

        // Repeat if necessary
        if (changed) {
            newStyleSpanRangeList = merge(newStyleSpanRangeList);
        }

        return newStyleSpanRangeList;
    }
}
