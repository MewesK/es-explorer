package net.mewk.fx.ese.highlighter;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.mewk.fx.control.codearea.StyleSpanRange;
import net.mewk.fx.control.codearea.StyleSpanRangeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorHighlighter implements Highlighter {

    private static final Logger LOG = LogManager.getLogger();
    private static final Pattern PATTERN;

    static {
        PATTERN = Pattern.compile("line (?<line>\\d+)");
    }

    public StyleSpanRangeBuilder compute(String text) {
        StyleSpanRangeBuilder styleSpanRangeBuilder = new StyleSpanRangeBuilder();

        try {
            new JsonParser().parse(text);
        } catch (JsonSyntaxException jse) {
            LOG.info(jse.getMessage());

            // Add error highlighting if errors occur
            Matcher matcher = PATTERN.matcher(jse.getMessage());
            if (matcher.find()) {
                String lineNumber = matcher.group("line");
                if (lineNumber != null) {
                    try {
                        int parsedLineNumber = Integer.parseInt(lineNumber);
                        int startOffset = StringUtils.ordinalIndexOf(text, "\n", parsedLineNumber - 1);
                        int endOffset = StringUtils.ordinalIndexOf(text, "\n", parsedLineNumber);

                        if (startOffset < 0) {
                            startOffset = 0;
                        }
                        if (startOffset > text.length()) {
                            startOffset = text.length();
                        }
                        if (endOffset < 0) {
                            endOffset = 0;
                        }
                        if (endOffset > text.length()) {
                            endOffset = text.length();
                        }
                        if (endOffset < startOffset) {
                            endOffset = startOffset;
                        }

                        styleSpanRangeBuilder.add(new StyleSpanRange(startOffset, endOffset, "error"));
                    } catch (NumberFormatException nfe) {
                        LOG.error(nfe.getMessage());
                    }
                }
            }
        }

        return styleSpanRangeBuilder;
    }
}
