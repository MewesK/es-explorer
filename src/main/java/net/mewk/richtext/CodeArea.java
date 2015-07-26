package net.mewk.richtext;

import net.mewk.ese.manager.HighlighterManager;
import org.fxmisc.richtext.LineNumberFactory;

public class CodeArea extends org.fxmisc.richtext.CodeArea {

    private final HighlighterManager highlighterManager = new HighlighterManager();

    public CodeArea() {
        getStylesheets().add(CodeArea.class.getResource("/net/mewk/ese/code-area.css").toExternalForm());
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        textProperty().addListener((obs, oldText, newText) -> applyHightlighting());
    }

    public void applyHightlighting() {
        setStyleSpans(0, highlighterManager.compute(getText()));
    }

    @Override
    public void replaceText(int start, int end, String text) {
        super.replaceText(start, end, text);
        // TODO: Work around - should not be encessary
        applyHightlighting();
    }
}
