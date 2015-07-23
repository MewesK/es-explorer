package net.mewk.ese;

import net.mewk.ese.manager.HighlighterManager;
import org.fxmisc.richtext.LineNumberFactory;

import javax.inject.Inject;

public class CodeArea extends org.fxmisc.richtext.CodeArea {

    @Inject
    private HighlighterManager highlighterManager;

    public CodeArea() {
        Main.INJECTOR.injectMembers(this);

        getStylesheets().add(CodeArea.class.getResource("/net/mewk/ese/code-area.css").toExternalForm());
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && newText.length() > 0) {
                applyHightlighting();
            }
        });
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
