package net.mewk.ese.highlighter;

import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorHighlighter implements Highlighter {

    private static final Logger LOG = LogManager.getLogger();
    private static final Pattern PATTERN;

    static {
        PATTERN = Pattern.compile("line (?<line>\\d+)");
    }

    public List<Span> compute(String text) {
        List<Span> spanList = Lists.newArrayList();

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
                        spanList.add(
                                new Span(
                                        StringUtils.ordinalIndexOf(text, "\n", parsedLineNumber - 2),
                                        StringUtils.ordinalIndexOf(text, "\n", parsedLineNumber - 1),
                                        "error"
                                )
                        );
                    } catch (NumberFormatException nfe) {
                        LOG.error(nfe.getMessage(), nfe);
                    }
                }
            }
        }

        return spanList;
    }
}
