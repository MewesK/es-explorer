package net.mewk.fx.esexplorer.highlighter;

import net.mewk.fx.control.codearea.StyleSpanRangeBuilder;

public interface Highlighter {
    StyleSpanRangeBuilder compute(String text);
}