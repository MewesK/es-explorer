package net.mewk.fx.control.codearea.style;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

public class StyleRangesBuilder extends ArrayList<StyleRange> {

    public StyleSpans<Collection<String>> create() {
        final StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();

        if (isEmpty()) {
            // Add blank span
            styleSpansBuilder.add(new StyleSpan<>(Lists.newArrayList(), 0));
        } else {
            // Merge
            StyleRange lastSpan = new StyleRange();
            for (StyleRange span : merge()) {
                // Add blank span if necessary
                if (lastSpan.getEnd() < span.getStart()) {
                    StyleRange blankSpan = new StyleRange(lastSpan.getEnd(), span.getStart());
                    styleSpansBuilder.add(blankSpan.createStyleSpan());
                }

                // Convert
                styleSpansBuilder.add(span.createStyleSpan());
                lastSpan = span;
            }
        }

        // Finalize
        return styleSpansBuilder.create();
    }

    private Stack<StyleRange> merge() {
        final Stack<StyleRange> styleRangeStack = new Stack<>();

        // Sort
        this.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

        // Overlay
        for (StyleRange span : this) {
            if (styleRangeStack.size() == 0) {
                styleRangeStack.push(span);
            } else {
                StyleRange topSpan = styleRangeStack.peek();
                if (topSpan.overlaps(span)) {
                    styleRangeStack.pop();
                    styleRangeStack.addAll(topSpan.overlay(span));
                } else {
                    styleRangeStack.push(span);
                }
            }
        }

        // Merge
        int size = styleRangeStack.size();
        for (int i = 0; i < size; i++) {
            if (i + 1 < size) {
                StyleRange current = styleRangeStack.get(i);
                StyleRange next = styleRangeStack.get(i + 1);

                // Overlap check
                if (next.contains(current.getEnd())) {
                    Collection<String> commonClasses = CollectionUtils.retainAll(current.getClassNameList(), next.getClassNameList());

                    // Class names check
                    if (commonClasses.size() == current.getClassNameList().size() && commonClasses.size() == next.getClassNameList().size()) {
                        // Merge operation
                        next.setStart(current.getStart());

                        // Mark for deletion
                        styleRangeStack.set(i, null);
                    }
                }
            }
        }

        // Delete all marked entries
        styleRangeStack.removeAll(Collections.<StyleRange>singleton(null));

        return styleRangeStack;
    }
}
