package net.mewk.fx.control.codearea;

import net.mewk.fx.ese.manager.HighlighterManager;
import org.fxmisc.richtext.LineNumberFactory;

public class CodeArea extends org.fxmisc.richtext.CodeArea {

    private final HighlighterManager highlighterManager = new HighlighterManager();

    public CodeArea() {
        getStylesheets().add(CodeArea.class.getResource("/net/mewk/fx/ese/code-area.css").toExternalForm());
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        textProperty().addListener((obs, oldText, newText) -> applyHighlighting());
    }

    @Override
    public void replaceText(int start, int end, String text) {
        super.replaceText(start, end, text);
        // TODO: Work around - should not be necessary
        applyHighlighting();
    }

    public void setText(String text) {
        replaceText(text);
    }

    private void applyHighlighting() {
        setStyleSpans(0, highlighterManager.compute(getText()));
    }
}
