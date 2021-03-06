package com.uncoder.visitor;

import com.uncoder.tree.*;

public interface Visitor<R, D> {
    R visit(Tree tree, D data);
    R visit(ExpressionTree tree, D data);
    R visit(LiteralTree tree, D data);
    R visit(BinaryTree tree, D data);
}
