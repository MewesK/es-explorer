package net.mewk.ese.manager;

import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.mewk.ese.highlighter.ErrorHighlighter;
import net.mewk.ese.highlighter.Highlighter;
import net.mewk.ese.highlighter.Span;
import net.mewk.ese.highlighter.SyntaxHighlighter;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

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

        // Merge lists
        final List<Span> combinedSpanList = Lists.newArrayList();
        for (Highlighter highlighter : highlighterList) {
            combinedSpanList.addAll(highlighter.compute(text));
        }

        // Sort combined list
        combinedSpanList.sort((o1, o2) -> Integer.compare(o1.getStart(), o2.getStart()));

        // Merge/stamp combined list
        final Stack<Span> results = new Stack<>();
        for (Span span : combinedSpanList) {
            if (results.size() == 0) {
                results.push(span);
            } else {
                Span topSpan = results.peek();
                if (topSpan.overlaps(span)) {
                    results.pop();
                    results.addAll(topSpan.stamp(span));
                } else {
                    results.push(span);
                }
            }
        }

        final StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();
        final List<Span> debug = Lists.newArrayList();

        Span lastSpan = new Span();
        for (Span span : results) {

            // Create blank span if necessary
            if (lastSpan.getEnd() < span.getStart()) {
                Span blankSpan = new Span(lastSpan.getEnd(), span.getStart(), "foo");

                // Convert to StyleSpans
                debug.add(blankSpan);
                styleSpansBuilder.add(new StyleSpan<>(blankSpan.getClassNameList(), blankSpan.length()));
            }

            // Convert to StyleSpans
            debug.add(span);
            styleSpansBuilder.add(new StyleSpan<>(span.getClassNameList(), span.length()));

            lastSpan = span;
        }

        return styleSpansBuilder.create();
    }

    public ObservableList<Highlighter> getHighlighterList() {
        return highlighterList;
    }

    public void setHighlighterList(ObservableList<Highlighter> highlighterList) {
        this.highlighterList = highlighterList;
    }
}
