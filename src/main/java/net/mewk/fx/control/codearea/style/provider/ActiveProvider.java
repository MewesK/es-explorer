package net.mewk.fx.control.codearea.style.provider;

import net.mewk.fx.control.codearea.CodeArea;
import net.mewk.fx.control.codearea.style.StyleRange;
import net.mewk.fx.control.codearea.style.StyleRangesBuilder;
import org.apache.commons.lang3.StringUtils;

public class ActiveProvider implements StyleRangeProvider {

    private final CodeArea codeArea;

    public ActiveProvider(CodeArea codeArea) {
        this.codeArea = codeArea;
    }

    public StyleRangesBuilder compute(String text) {
        StyleRangesBuilder styleRangesBuilder = new StyleRangesBuilder();

        if (codeArea != null) {
            int currentParagraph = codeArea.getCurrentParagraph();
            int startOffset = StringUtils.ordinalIndexOf(text, "\n", currentParagraph);
            int endOffset = StringUtils.ordinalIndexOf(text, "\n", currentParagraph + 1);

            if (startOffset == -1) {
                startOffset = 0;
            }
            if (endOffset == -1) {
                endOffset = text.length();
            }

            styleRangesBuilder.add(new StyleRange(startOffset, endOffset, "active"));
        }

        return styleRangesBuilder;
    }
}
