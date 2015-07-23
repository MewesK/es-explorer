package net.mewk.richtext;

import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class StyleSpanRangeBuilder extends ArrayList<StyleSpanRange> {

    public StyleSpans<Collection<String>> create() {
        // Sort combined list
        this.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

        // Merge/stamp combined list


        final StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();

        StyleSpanRange lastSpan = new StyleSpanRange();
        for (StyleSpanRange span : merge()) {
            // Create blank span if necessary
            if (lastSpan.getEnd() < span.getStart()) {
                StyleSpanRange blankSpan = new StyleSpanRange(lastSpan.getEnd(), span.getStart());
                styleSpansBuilder.add(blankSpan.createStyleSpan());
            }

            styleSpansBuilder.add(span.createStyleSpan());
            lastSpan = span;
        }

        return styleSpansBuilder.create();
    }

    private Stack<StyleSpanRange> merge() {
        final Stack<StyleSpanRange> styleSpanRangeStack = new Stack<>();

        for (StyleSpanRange span : this) {
            if (styleSpanRangeStack.size() == 0) {
                styleSpanRangeStack.push(span);
            } else {
                StyleSpanRange topSpan = styleSpanRangeStack.peek();
                if (topSpan.overlaps(span)) {
                    styleSpanRangeStack.pop();
                    styleSpanRangeStack.addAll(topSpan.stamp(span));
                } else {
                    styleSpanRangeStack.push(span);
                }
            }
        }

        return styleSpanRangeStack;
    }
}