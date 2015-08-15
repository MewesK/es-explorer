package net.mewk.fx.control.codearea;

import com.google.common.collect.Lists;
import net.mewk.fx.control.codearea.style.StyleRangesBuilder;
import net.mewk.fx.control.codearea.style.provider.StyleRangeProvider;

import java.util.List;

public class CodeArea extends org.fxmisc.richtext.CodeArea {

    private List<StyleRangeProvider> textStyleRangeProviderList = Lists.newArrayList();
    private StyleRangesBuilder textStyleRangesBuilder;

    private List<StyleRangeProvider> stateStyleRangeProviderList = Lists.newArrayList();
    private StyleRangesBuilder stateStyleRangesBuilder;

    public CodeArea() {

        // Initialize auto highlighting (changed by user)
        textProperty().addListener((obs, oldText, newText) -> computeTextStyleSpans());

        // Initialize current line highlighting
        currentParagraphProperty().addListener((observable, oldValue, newValue) -> createStateStyleRangesBuilder());

        // Initialize paragraph graphic factory
        setParagraphGraphicFactory(new LineNumberFactory(this));

        // Load stylesheets
        getStylesheets().add(CodeArea.class.getResource("style.css").toExternalForm());
    }

    public void computeStyleSpans() {
        final StyleRangesBuilder combinedStyleRangesBuilder = new StyleRangesBuilder();

        if (textStyleRangesBuilder != null) {
            combinedStyleRangesBuilder.addAll(textStyleRangesBuilder);
        }

        if (stateStyleRangesBuilder != null) {
            combinedStyleRangesBuilder.addAll(stateStyleRangesBuilder);
        }

        setStyleSpans(0, combinedStyleRangesBuilder.create());
    }

    @Override
    public void replaceText(int start, int end, String text) {
        super.replaceText(start, end, text);

        // Auto highlighting (changed by code)
        computeTextStyleSpans();
    }

    public void computeTextStyleSpans() {
        textStyleRangesBuilder = createStyleRangesBuilder(textStyleRangeProviderList);
        computeStyleSpans();
    }

    public void createStateStyleRangesBuilder() {
        stateStyleRangesBuilder = createStyleRangesBuilder(stateStyleRangeProviderList);
        computeStyleSpans();
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

    public List<StyleRangeProvider> getTextStyleRangeProviderList() {
        return textStyleRangeProviderList;
    }

    public List<StyleRangeProvider> getStateStyleRangeProviderList() {
        return stateStyleRangeProviderList;
    }

    public void setText(String text) {
        replaceText(text);
    }
}
