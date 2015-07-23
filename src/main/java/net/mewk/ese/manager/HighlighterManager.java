package net.mewk.ese.manager;

import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.mewk.ese.highlighter.ErrorHighlighter;
import net.mewk.ese.highlighter.Highlighter;
import net.mewk.ese.highlighter.SyntaxHighlighter;
import net.mewk.richtext.StyleSpanRangeBuilder;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

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
        if (text == null || text.isEmpty()) {
            final StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();
            styleSpansBuilder.add(new StyleSpan<>(Lists.newArrayList(), 0));
            return styleSpansBuilder.create();
        }

        // Merge layers
        final StyleSpanRangeBuilder combinedStyleSpanRangeBuilder = new StyleSpanRangeBuilder();

        for (Highlighter highlighter : highlighterList) {
            combinedStyleSpanRangeBuilder.addAll(highlighter.compute(text));
        }

        return combinedStyleSpanRangeBuilder.create();
    }

    public ObservableList<Highlighter> getHighlighterList() {
        return highlighterList;
    }

    public void setHighlighterList(ObservableList<Highlighter> highlighterList) {
        this.highlighterList = highlighterList;
    }
}
