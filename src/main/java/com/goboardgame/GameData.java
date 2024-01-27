package com.goboardgame;

import java.io.Serializable;

public class GameData implements Serializable {
    private GoGame goGame;

    public GameData(GoGame goGame) {
        this.goGame = goGame;
    }

    public GoGame getGoGame() {
        return goGame;
    }
}
