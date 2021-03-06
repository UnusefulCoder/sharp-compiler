package com.uncoder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.uncoder.token.Token;
import com.uncoder.token.Tokens;
import com.uncoder.util.Position;

public class Lexer {
    private static final String NUMBER_POSTFIX = "lLfFdD";

    private final BufferedReader reader;

    private int line = 1;
    private int column = 0;

    private int codePoint;

    private boolean isInitialized;

    public Lexer(final String filename) throws IOException {
        this.reader = new BufferedReader(new FileReader(filename));
    }

    public Token next() {
        if (!isInitialized) {
            nextCodePoint();
    
            isInitialized = true;
        }

        Position position = new Position(line, column);

        if (Character.isDigit(codePoint)) {
            return getNumberToken(position);
        }

        if (Character.isJavaIdentifierStart(codePoint)) {
            return getIdentifierToken(position);
        }

        return getSymbolToken(position);
    }

    private Token getNumberToken(Position position) {
		StringBuilder string = new StringBuilder();
		
		do string.append(advanceCodePoint());
        while(Character.isDigit(codePoint));

        if (codePoint == '.') {
            do string.append(advanceCodePoint());
            while(Character.isDigit(codePoint));
        }

        if (NUMBER_POSTFIX.codePoints().anyMatch(cp -> cp == codePoint)) {
            string.append(advanceCodePoint());
        }

        return new Token(string.toString(), Tokens.NUMBER, position);
    }

    private Token getIdentifierToken(Position position) {
		StringBuilder string = new StringBuilder();

        do string.append(advanceCodePoint());
        while(Character.isJavaIdentifierPart(codePoint));

        return new Token(string.toString(), Tokens.IDENTIFIER, position);
    }

    private Token getSymbolToken(Position position) {
        if (codePoint == -1) {
            return new Token(advanceCodePoint(), Tokens.EOF, position);
        }

        return new Token(advanceCodePoint(), Tokens.SYMBOL, position);
    }

    private int nextCodePoint() {
        try {
            codePoint = reader.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (codePoint == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }

        return codePoint;
    }

    private String advanceCodePoint() {
        int backup = codePoint;

        nextCodePoint();

        return switch(backup) {
            case '\n' -> "\\n";
            case '\b' -> "\\b";
            case '\r' -> "\\r";
            case '\f' -> "\\f";
            case '\t' -> "\\t";
            case '\0', -1 -> "\\0";
            default -> Character.toString(backup);
        };
    }
}
