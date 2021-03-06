package com.uncoder.tree;

import com.uncoder.token.Token;
import com.uncoder.visitor.Visitor;

public class UnaryTree implements ExpressionTree {
    private final Token operator;
    private final Tree operand;

    private final boolean isPostfix;

    public UnaryTree(Token operator, Tree operand) {
        this(operator, operand, false);
    }

    public UnaryTree(Token operator, Tree operand, boolean isPostfix) {
        this.operator = operator;
        this.operand = operand;
        this.isPostfix = isPostfix;
    }

    public Tree getThisOrOperand() {
        return operator == null ? operand : this;
    }

    @Override
    public String toString() {
        return operator + " " + operand;
    }

    @Override
    public <R, D> R accept(Visitor<R, D> visitor, D data) {
        return visitor.visit(this, data);
    }
    
    public Token getOperator() {
        return operator;
    }

    public Tree getOperand() {
        return operand;
    }

    public boolean isPostfix() {
        return isPostfix;
    }
}
