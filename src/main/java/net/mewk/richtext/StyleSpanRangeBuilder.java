package net.mewk.richtext;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

public class StyleSpanRangeBuilder extends ArrayList<StyleSpanRange> {

    public StyleSpans<Collection<String>> create() {
        final StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();

        if (isEmpty()) {
            // Add blank span
            styleSpansBuilder.add(new StyleSpan<>(Lists.newArrayList(), 0));
        } else {
            // Merge
            StyleSpanRange lastSpan = new StyleSpanRange();
            for (StyleSpanRange span : merge()) {
                // Add blank span if necessary
                if (lastSpan.getEnd() < span.getStart()) {
                    StyleSpanRange blankSpan = new StyleSpanRange(lastSpan.getEnd(), span.getStart());
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

    private Stack<StyleSpanRange> merge() {
        final Stack<StyleSpanRange> styleSpanRangeStack = new Stack<>();

        // Sort
        this.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

        // Overlay
        for (StyleSpanRange span : this) {
            if (styleSpanRangeStack.size() == 0) {
                styleSpanRangeStack.push(span);
            } else {
                StyleSpanRange topSpan = styleSpanRangeStack.peek();
                if (topSpan.overlaps(span)) {
                    styleSpanRangeStack.pop();
                    styleSpanRangeStack.addAll(topSpan.overlay(span));
                } else {
                    styleSpanRangeStack.push(span);
                }
            }
        }

        // Merge
        int size = styleSpanRangeStack.size();
        for (int i = 0; i < size; i++) {
            if (i + 1 < size) {
                StyleSpanRange current = styleSpanRangeStack.get(i);
                StyleSpanRange next = styleSpanRangeStack.get(i + 1);

                // Overlap check
                if (next.contains(current.getEnd())) {
                    Collection<String> commonClasses = CollectionUtils.retainAll(current.getClassNameList(), next.getClassNameList());

                    // Class names check
                    if (commonClasses.size() == current.getClassNameList().size() && commonClasses.size() == next.getClassNameList().size()) {
                        // Merge operation
                        next.setStart(current.getStart());

                        // Mark for deletion
                        styleSpanRangeStack.set(i, null);
                    }
                }
            }
        }

        // Delete all marked entries
        styleSpanRangeStack.removeAll(Collections.<StyleSpanRange>singleton(null));

        return styleSpanRangeStack;
    }
}
