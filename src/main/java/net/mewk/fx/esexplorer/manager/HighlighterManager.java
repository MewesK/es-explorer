package net.mewk.fx.esexplorer.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.mewk.fx.control.codearea.StyleSpanRangeBuilder;
import net.mewk.fx.esexplorer.highlighter.ErrorHighlighter;
import net.mewk.fx.esexplorer.highlighter.Highlighter;
import net.mewk.fx.esexplorer.highlighter.SyntaxHighlighter;
import org.fxmisc.richtext.StyleSpans;

import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class HighlighterManager {

    private ObservableList<Highlighter> highlighterList = FXCollections.observableArrayList();

    public HighlighterManager() {
        highlighterList.addAll(
                new SyntaxHighlighter(),
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
