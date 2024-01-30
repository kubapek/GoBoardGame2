package com.goboardgame;

import java.io.Serializable;

public class EndGameData implements Serializable {
    private final GoGame goGame;

    public EndGameData(GoGame goGame) {
        this.goGame = goGame;
    }

    public GoGame getEndGoGame() {
        return goGame;
    }
}
