package net.mewk.fx.control.codearea;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import net.mewk.fx.control.codearea.style.StyleRangesBuilder;
import net.mewk.fx.control.codearea.style.provider.StyleRangeProvider;

import java.util.List;

public class CodeArea extends org.fxmisc.richtext.CodeArea {

    private ObservableList<StyleRangeProvider> styleRangeProviderList = FXCollections.observableArrayList();

    public CodeArea() {

        // Initialize highlighting
        textProperty().addListener((obs, oldText, newText) -> {
            refreshStyles();
        });
        currentParagraphProperty().addListener((observable, oldValue, newValue) -> {
            refreshStyles();
        });
        styleRangeProviderList.addListener((ListChangeListener<StyleRangeProvider>) c -> {
            refreshStyles();
        });

        // Initialize paragraph graphic factory
        setParagraphGraphicFactory(new LineNumberFactory(this));

        // Load stylesheets
        getStylesheets().add(CodeArea.class.getResource("style.css").toExternalForm());
    }

    @Override
    public void replaceText(int start, int end, String text) {
        super.replaceText(start, end, text);

        // Refrsh code highlighting
        refreshStyles();
    }

    public void refreshStyles() {
        final StyleRangesBuilder combinedStyleRangesBuilder = new StyleRangesBuilder();

        combinedStyleRangesBuilder.addAll(createStyleRangesBuilder(styleRangeProviderList));

        setStyleSpans(0, combinedStyleRangesBuilder.create());
    }

    private StyleRangesBuilder createStyleRangesBuilder(List<StyleRangeProvider> stateStyleRangeProviderList) {
        final StyleRangesBuilder combinedStyleRangesBuilder = new StyleRangesBuilder();
        final String text = getText();

        // Merge layers
        if (text != null && !text.isEmpty()) {
            for (StyleRangeProvider styleRangeProvider : stateStyleRangeProviderList) {
                combinedStyleRangesBuilder.addAll(styleRangeProvider.compute(text));
            }
        }

        return combinedStyleRangesBuilder;
    }

    public ObservableList<StyleRangeProvider> getStyleRangeProviderList() {
        return styleRangeProviderList;
    }

    public void setText(String text) {
        replaceText(0, text.length(), text);
    }
}
