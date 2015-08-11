package net.mewk.fx.ese.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.mewk.fx.control.codearea.StyleSpanRangeBuilder;
import net.mewk.fx.ese.highlighter.*;
import org.fxmisc.richtext.StyleSpans;

import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class HighlighterManager {

    private ObservableList<Highlighter> highlighterList = FXCollections.observableArrayList();

    public HighlighterManager() {
        highlighterList.addAll(
                new DefaultHighlighter(),
                new SyntaxHighlighter(),
                new KeywordHighlighter(),
                new ErrorHighlighter()
        );
    }

    public StyleSpans<Collection<String>> compute(String text) {
        final StyleSpanRangeBuilder combinedStyleSpanRangeBuilder = new StyleSpanRangeBuilder();

        // Merge layers
        if (text != null && !text.isEmpty()) {
            for (Highlighter highlighter : highlighterList) {
                combinedStyleSpanRangeBuilder.addAll(highlighter.compute(text));
            }
        }

        return combinedStyleSpanRangeBuilder.create();
    }

    // Property access

    public ObservableList<Highlighter> getHighlighterList() {
        return highlighterList;
    }

    public void setHighlighterList(ObservableList<Highlighter> highlighterList) {
        this.highlighterList = highlighterList;
    }
}
