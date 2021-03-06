package com.uncoder.tree;

import com.uncoder.token.Token;
import com.uncoder.visitor.Visitor;

public abstract class BinaryTree implements ExpressionTree {
    private final Token operator;
    private final Tree left;
    private final Tree right;

    public BinaryTree(Token operator, Tree left, Tree right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public static final class AdditionTree extends BinaryTree {
        public AdditionTree(Token operator, Tree left, Tree right) {
            super(operator, left, right);
        }
    }

    public static final class MultiplicationTree extends BinaryTree {
        public MultiplicationTree(Token operator, Tree left, Tree right) {
            super(operator, left, right);
        }
    }

    public Tree getThisOrLeft() {
        return operator == null ? left : this;
    }

    @Override
    public String toString() {
        return operator + " " + left + " " + right;
    }

    @Override
    public <R, D> R accept(Visitor<R, D> visitor, D data) {
        return visitor.visit(this, data);
    }
    
    public Token getOperator() {
        return operator;
    }

    public Tree getLeft() {
        return left;
    }

    public Tree getRight() {
        return right;
    }
}
