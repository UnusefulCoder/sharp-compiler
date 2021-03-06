package com.uncoder.tree;

import com.uncoder.token.Token;
import com.uncoder.visitor.Visitor;

public class LiteralTree implements ExpressionTree {
    private final Token token;

    public LiteralTree(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token.toString();
    }

    @Override
    public <R, D> R accept(Visitor<R, D> visitor, D data) {
        return visitor.visit(this, data);
    }

    public Token getToken() {
        return token;
    }
}