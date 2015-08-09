package net.mewk.fx.control.codearea.style.provider;

import net.mewk.fx.control.codearea.style.StyleRangesBuilder;

public interface StyleRangeProvider {
    StyleRangesBuilder compute(String text);
}
