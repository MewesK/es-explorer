package net.mewk.ese.highlighter;

import java.util.List;

public interface Highlighter {
    List<Span> compute(String text);
}
