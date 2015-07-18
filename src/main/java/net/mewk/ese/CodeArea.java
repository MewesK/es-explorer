package net.mewk.ese;

import org.fxmisc.richtext.LineNumberFactory;

public class CodeArea extends org.fxmisc.richtext.CodeArea {

    public CodeArea() {
        getStylesheets().add(CodeArea.class.getResource("/net/mewk/ese/code-area.css").toExternalForm());
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        textProperty().addListener((obs, oldText, newText) -> {
            setStyleSpans(0, Highlighter.computeHighlighting(newText));
        });
    }
}
