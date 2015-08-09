package net.mewk.fx.control.codearea;

import net.mewk.fx.control.codearea.style.StyleRange;
import net.mewk.fx.control.codearea.style.StyleRangesBuilder;
import org.fxmisc.richtext.StyleSpans;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class StyleSpanRangeBuilderTest {

    @Test
    public void testCreate() throws Exception {
        // i: 00 01 02 03 04 05 06 07 08 09 10 11 12
        // a:    -----       --             --------
        StyleRangesBuilder styleRangesBuilder1 = new StyleRangesBuilder();
        styleRangesBuilder1.add(new StyleRange(1, 3, "a"));
        styleRangesBuilder1.add(new StyleRange(5, 6, "a"));
        styleRangesBuilder1.add(new StyleRange(10, 11, "a"));
        styleRangesBuilder1.add(new StyleRange(11, 13, "a"));

        // i: 00 01 02 03 04 05 06 07 08 09 10 11 12
        // b:       -----------
        // c:                -----       --------
        StyleRangesBuilder styleRangesBuilder2 = new StyleRangesBuilder();
        styleRangesBuilder2.add(new StyleRange(2, 6, "b"));
        styleRangesBuilder2.add(new StyleRange(5, 7, "c"));
        styleRangesBuilder2.add(new StyleRange(9, 12, "c"));

        // i: 00 01 02 03 04 05 06 07 08 09 10 11 12
        // a:    -----       --             --------
        // b:       -----------
        // c:                -----       --------
        styleRangesBuilder1.addAll(styleRangesBuilder2);

        // . (1 class)
        // - (2 classes)
        // _ (3 classes)

        // i: 00 01 02 03 04 05 06 07 08 09 10 11 12
        //       .. -- .. .. __ ..       .. -- -- ..
        StyleSpans<Collection<String>> styleSpans = styleRangesBuilder1.create();

        assertEquals(1, styleSpans.getStyleSpan(0).getLength());
        assertEquals(0, styleSpans.getStyleSpan(0).getStyle().size());

        assertEquals(1, styleSpans.getStyleSpan(1).getLength());
        assertEquals(1, styleSpans.getStyleSpan(1).getStyle().size());

        assertEquals(1, styleSpans.getStyleSpan(2).getLength());
        assertEquals(2, styleSpans.getStyleSpan(2).getStyle().size());

        assertEquals(2, styleSpans.getStyleSpan(3).getLength());
        assertEquals(1, styleSpans.getStyleSpan(3).getStyle().size());

        assertEquals(1, styleSpans.getStyleSpan(4).getLength());
        assertEquals(3, styleSpans.getStyleSpan(4).getStyle().size());

        assertEquals(1, styleSpans.getStyleSpan(5).getLength());
        assertEquals(1, styleSpans.getStyleSpan(5).getStyle().size());

        assertEquals(2, styleSpans.getStyleSpan(6).getLength());
        assertEquals(0, styleSpans.getStyleSpan(6).getStyle().size());

        assertEquals(1, styleSpans.getStyleSpan(7).getLength());
        assertEquals(1, styleSpans.getStyleSpan(7).getStyle().size());

        assertEquals(2, styleSpans.getStyleSpan(8).getLength());
        assertEquals(2, styleSpans.getStyleSpan(8).getStyle().size());

        assertEquals(1, styleSpans.getStyleSpan(9).getLength());
        assertEquals(1, styleSpans.getStyleSpan(9).getStyle().size());
    }
}