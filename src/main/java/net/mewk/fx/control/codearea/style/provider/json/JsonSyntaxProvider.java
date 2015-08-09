package net.mewk.fx.control.codearea.style.provider.json;

import net.mewk.fx.control.codearea.style.provider.AbstractSyntaxProvider;

public class JsonSyntaxProvider extends AbstractSyntaxProvider {

    static {
        // Define sub patterns
        SUB_PATTERNS.add(new SyntaxPattern("\"([^\"\\\\]|\\\\.)*\"\\s*:", "KEY", "key"));
        SUB_PATTERNS.add(new SyntaxPattern("\\{|\\}", "BRACE", "brace"));
        SUB_PATTERNS.add(new SyntaxPattern("\\[|\\]", "BRACKET", "bracket"));
        SUB_PATTERNS.add(new SyntaxPattern("\\,", "COMMA", "comma"));

        SUB_PATTERNS.add(new SyntaxPattern("\"([^\"\\\\]|\\\\.)*\"", "STRING", "string"));
        SUB_PATTERNS.add(new SyntaxPattern("(?i)(true|false)", "BOOL", "bool"));
        SUB_PATTERNS.add(new SyntaxPattern("(?i)(null)", "NULL", "null"));
        SUB_PATTERNS.add(new SyntaxPattern("(\\d+)", "INT", "int"));
        SUB_PATTERNS.add(new SyntaxPattern("(\\d*\\.\\d+)", "DOUBLE", "double"));

        buildPattern();
    }
}
