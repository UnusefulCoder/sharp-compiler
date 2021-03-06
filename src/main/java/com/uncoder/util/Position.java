package com.uncoder.util;

public class Position {
    private final int line;
    private final int column;

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return getLine() + ":" + getColumn();
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}