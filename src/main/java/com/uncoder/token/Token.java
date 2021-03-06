package com.uncoder.token;

import com.uncoder.util.Position;

public class Token {
    private final String image;
    private final Tokens type;
    private final Position position;

    private boolean isBlankSpace;

    public Token(String image, Tokens type, Position position) {
        this.image = image;
        this.type = type;
        this.position = position;

        this.isBlankSpace = image.equals("\\n")
            || image.equals("\\b")
            || image.equals("\\f")
            || image.equals("\\r")
            || image.equals("\\t")
            || image.equals(" ");
    }

    @Override
    public String toString() {
        return getImage() + "[" + position + "]";
    }

    public String getImage() {
        return image;
    }

    public Tokens getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isType(Tokens type) {
        return this.type == type;
    }

	public boolean isBlankSpace() {
		return isBlankSpace;
	}
}
