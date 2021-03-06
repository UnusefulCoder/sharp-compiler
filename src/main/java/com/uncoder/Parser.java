package com.uncoder;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.uncoder.token.Token;
import com.uncoder.tree.*;

import static com.uncoder.token.Tokens.*;
import com.uncoder.tree.BinaryTree.*;
import com.uncoder.util.Position;

public class Parser {
    private final Lexer lex;

    private final Deque<Tree> deque;

    private Token current;

    public Parser(final String filename) throws IOException {
        lex = new Lexer(filename);

        deque = new ArrayDeque<>();
    }

    public List<Tree> parse() {
        next();

        while (current.isBlankSpace()) {
            next();
        }

        while (!current.isType(EOF)) {
            reducePrefixes();
            reduceLiterals();
            reduceMultiplications();
            reduceAdditions();

            while (current.isBlankSpace()) {
                next();
            }
        }

        if (deque.peek() instanceof BinaryTree binaryTree) {
            deque.poll();
            deque.add(binaryTree.getThisOrLeft());
        }

        return deque.stream().collect(Collectors.toUnmodifiableList());
    }

    private void reducePrefixes() {
        final String image = current.getImage();

        if ("+".equals(image) || "-".equals(image)) {
            reduceDoubleBinary(
                (operator, prefix) -> new UnaryTree(
                    new Token(prefix + prefix, SYMBOL, operator.getPosition()),
                    requireIdentifier()
                )
            );
        } else if ("*".equals(image) || "&".equals(image)) {
            final Token operator = advance();

            ignoreBlankSpaces();

            deque.add(new UnaryTree(operator, requireIdentifier()));
        } else if ("(".equals(image)) {
            next();

            ignoreBlankSpaces();

            final Token operator = current;

            reducePrefixes();
            reduceLiterals();

            if (deque.peek() instanceof LiteralTree) {
                ignoreBlankSpaces();
                
                if (")".equals(current.getImage())) {
                    next();

                    ignoreBlankSpaces();

                    reducePrefixes();
                    reduceLiterals();

                    if (deque.peek() instanceof LiteralTree) {
                        deque.poll();
                        deque.add(
                            new UnaryTree(
                                new Token("(" + operator.getImage() + ")", SYMBOL, operator.getPosition()),
                                deque.poll()
                            )
                        );
                    } else {
                        reduceMultiplications();
                        reduceAdditions();
                    }

                    return;
                }
            }

            do {
                reduceMultiplications();
                reduceAdditions();
    
                ignoreBlankSpaces();

                if ("\\0".equals(current.getImage())) {
                    deque.add(
                        new ErroneousTree(
                            "[ERROR] Missing ')' at position " + current.getPosition()
                            + ". Found " + current.getImage()
                        )
                    );

                    return;
                } else if (")".equals(current.getImage())) {
                    break;
                } else {
                    reducePrefixes();
                    reduceLiterals();
                }
            } while (!")".equals(current.getImage()));

            next();

            reduceAdditions();
            while (true) {
                ignoreBlankSpaces();
    
                final String image0 = current.getImage();
    
                if (!("*".equals(image0) || "/".equals(image0) || "%".equals(image0))) {
                    break;
                }

                reduceInnerMultiplications(advance());
            }
        }
    }

    private void reduceLiterals() {
        switch (current.getType()) {
            case IDENTIFIER -> {
                deque.add(new LiteralTree(advance()));

                final String image = current.getImage();

                if ("+".equals(image) || "-".equals(image)) {
                    reduceDoubleBinary(
                        (operator, postfix) -> new UnaryTree(
                            new Token(postfix + postfix, SYMBOL, operator.getPosition()),
                            deque.poll(),
                            true
                        )
                    );
                }
            }
            case NUMBER -> deque.add(new LiteralTree(advance()));
            default -> {}
        }
    }

    private void reduceMultiplications() {
        Tree tree = deque.peek();

        if (tree instanceof LiteralTree
            || tree instanceof UnaryTree
            || tree instanceof MultiplicationTree
        ) {
            while (true) {
                ignoreBlankSpaces();
    
                final String image = current.getImage();
    
                if (!("*".equals(image) || "/".equals(image) || "%".equals(image))) {
                    break;
                }

                reduceInnerMultiplications(advance());
            }
        }
    }

    private void reduceAdditions() {
        Tree tree = deque.peek();

        if (tree instanceof LiteralTree
            || tree instanceof UnaryTree
            || tree instanceof MultiplicationTree
            || tree instanceof AdditionTree
        ) {
            while (true) {
                ignoreBlankSpaces();

                final String image = current.getImage();

                if (!("+".equals(image) || "-".equals(image))) {
                    break;
                }

                reduceInnerAdditions(advance());
            }
        }
    }

    private Tree requireIdentifier() {
        if (current.isType(IDENTIFIER)) {
            reduceLiterals();

            return deque.poll();
        } else {
            return new ErroneousTree(
                "[ERROR] Identifier expected at position " + current.getPosition()
                + ", but found '" + current.getImage() + "'."
            );
        }
    }

    private void reduceDoubleBinary(BiFunction<Token, String, UnaryTree> function) {
        final Token operator = advance();
        final String image = operator.getImage();

        if (image.equals(current.getImage())) {
            do {
                next();
            } while (current.isBlankSpace());
            
            deque.add(function.apply(operator, image));
        } else {
            reduceInnerAdditions(operator);
        }
    }

    private void reduceInnerMultiplications(final Token operator) {
        ignoreBlankSpaces();

        final Tree left = deque.poll();

        reducePrefixes();
        reduceLiterals();

        final Tree right = deque.peek() != null
            ? deque.poll()
            : new ErroneousTree(
                "[ERROR] Literal expected at position " + current.getPosition()
                + ", but found '" + current.getImage() + "'."
            );

        deque.add(new MultiplicationTree(operator, left, right));
    }

    private void reduceInnerAdditions(final Token operator) {
        ignoreBlankSpaces();

        final Tree left = deque.poll();

        reducePrefixes();
        reduceLiterals();
        reduceMultiplications();

        final Tree right = deque.peek() != null
            ? deque.poll()
            : new ErroneousTree(
                "[ERROR] Literal expected at position " + current.getPosition()
                + ", but found '" + current.getImage() + "'."
            );

        deque.add(new AdditionTree(operator, left, right));
    }

    private void ignoreBlankSpaces() {
        while (current.isBlankSpace()) {
            next();
        }
    }

    private Token next() {
        current = lex.next();

        return current;
    }

    private Token advance() {
        Token backup = current;

        next();

        return backup;
    }
}
