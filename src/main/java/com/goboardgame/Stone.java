package com.goboardgame;

import java.io.Serializable;

public class Stone implements Serializable {
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

    @Override
    public String toString() {
        return "Stone{" +
                "color=" + color +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
