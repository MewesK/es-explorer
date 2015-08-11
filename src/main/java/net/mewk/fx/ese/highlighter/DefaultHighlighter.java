package net.mewk.fx.ese.highlighter;

import net.mewk.fx.control.codearea.StyleSpanRange;
import net.mewk.fx.control.codearea.StyleSpanRangeBuilder;

public class DefaultHighlighter implements Highlighter {

    public StyleSpanRangeBuilder compute(String text) {
        StyleSpanRangeBuilder styleSpanRangeBuilder = new StyleSpanRangeBuilder();

        styleSpanRangeBuilder.add(new StyleSpanRange(0, text.length(), "text"));

        return styleSpanRangeBuilder;
    }
}
