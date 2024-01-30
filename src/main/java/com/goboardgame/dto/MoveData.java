package com.goboardgame.dto;

import java.io.Serializable;

public class MoveData implements Serializable {
    private final int x;
    private final int y;

    public MoveData(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
