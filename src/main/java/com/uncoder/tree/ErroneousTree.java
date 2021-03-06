package com.uncoder.tree;

import com.uncoder.visitor.Visitor;

public class ErroneousTree implements Tree {
    private final String message;

    public ErroneousTree(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public <R, D> R accept(Visitor<R, D> visitor, D data) {
        return visitor.visit(this, data);
    }
    
    public String getMessage() {
        return message;
    }
}
