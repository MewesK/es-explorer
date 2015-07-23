package net.mewk.ese.highlighter;

import net.mewk.richtext.StyleSpanRangeBuilder;

import java.util.List;

public interface Highlighter {
    StyleSpanRangeBuilder compute(String text);
}
