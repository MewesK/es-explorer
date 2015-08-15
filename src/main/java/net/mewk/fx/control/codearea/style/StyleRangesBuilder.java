package net.mewk.fx.control.codearea.style;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StyleRangesBuilder extends ArrayList<StyleRange> {

    public StyleSpans<Collection<String>> create() {
        final StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();

        if (isEmpty()) {
            // Add blank span
            styleSpansBuilder.add(new StyleSpan<>(Lists.newArrayList(), 0));
        } else {
            // Overlay
            List<StyleRange> styleRangeList = overlay(this);

            // Merge
            styleRangeList = merge(styleRangeList);

            // Sort
            styleRangeList.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

            // Convert
            StyleRange lastSpan = new StyleRange();
            for (StyleRange span : styleRangeList) {
                // Add blank span if necessary
                if (lastSpan.getEnd() < span.getStart()) {
                    StyleRange blankSpan = new StyleRange(lastSpan.getEnd(), span.getStart());
                    styleSpansBuilder.add(blankSpan.createStyleSpan());
                }

                styleSpansBuilder.add(span.createStyleSpan());
                lastSpan = span;
            }
        }

        // Finalize
        return styleSpansBuilder.create();
    }

    private List<StyleRange> overlay(List<StyleRange> StyleRangeList) {
        List<StyleRange> newStyleRangeList = Lists.newArrayList();

        // Sort
        StyleRangeList.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

        boolean changed = false;
        for (StyleRange span : StyleRangeList) {
            if (newStyleRangeList.size() != 0) {
                int lastIndex = newStyleRangeList.size() - 1;
                StyleRange topSpan = newStyleRangeList.get(lastIndex);
                if (topSpan.overlaps(span)) {
                    changed = true;

                    // Overlay operation
                    newStyleRangeList.remove(lastIndex);
                    newStyleRangeList.addAll(topSpan.overlay(span));
                    continue;
                }
            }

            newStyleRangeList.add(span);
        }

        // Repeat if changed
        if (changed) {
            newStyleRangeList = overlay(newStyleRangeList);
        }

        return newStyleRangeList;
    }

    private List<StyleRange> merge(List<StyleRange> StyleRangeList) {
        List<StyleRange> newStyleRangeList = Lists.newArrayList();

        // Sort
        StyleRangeList.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

        boolean changed = false;
        int size = StyleRangeList.size();
        for (int i = 0; i < size; i++) {
            if (i + 1 < size) {
                StyleRange current = StyleRangeList.get(i);
                StyleRange next = StyleRangeList.get(i + 1);

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
                    newStyleRangeList.add(current);
                }
            }
        }

        // Repeat if necessary
        if (changed) {
            newStyleRangeList = merge(newStyleRangeList);
        }

        return newStyleRangeList;
    }
}
