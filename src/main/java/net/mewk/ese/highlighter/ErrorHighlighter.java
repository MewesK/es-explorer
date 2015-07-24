package net.mewk.ese.highlighter;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.mewk.richtext.StyleSpanRange;
import net.mewk.richtext.StyleSpanRangeBuilder;
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
            LOG.error(jse.getMessage(), jse);

            // Add error highlighting if errors occur
            Matcher matcher = PATTERN.matcher(jse.getMessage());
            if (matcher.find()) {
                String lineNumber = matcher.group("line");
                if (lineNumber != null) {
                    try {
                        int parsedLineNumber = Integer.parseInt(lineNumber);
                        styleSpanRangeBuilder.add(
                                new StyleSpanRange(
                                        StringUtils.ordinalIndexOf(text, "\n", parsedLineNumber - 2),
                                        StringUtils.ordinalIndexOf(text, "\n", parsedLineNumber - 1),
                                        "error"
                                )
                        );
                    } catch (NumberFormatException nfe) {
                        LOG.error(nfe.getMessage());
                    }
                }
            }
        }

        return styleSpanRangeBuilder;
    }
}
