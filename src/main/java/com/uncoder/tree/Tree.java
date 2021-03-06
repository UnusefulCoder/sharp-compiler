package com.uncoder.tree;

import com.uncoder.visitor.Visitor;

public interface Tree {
    <R, D> R accept(Visitor<R, D> visitor, D data);
}
