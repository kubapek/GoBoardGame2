package com.goboardgame;

public class Stone {
    private final StoneColor color;
    private final int x;
    private final int y;

    public Stone(StoneColor color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public StoneColor getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public enum StoneColor {
        BLACK, WHITE
    }
}
