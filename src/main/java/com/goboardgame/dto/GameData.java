package com.goboardgame.dto;

import com.goboardgame.GoGame;

import java.io.Serializable;

public class GameData implements Serializable {
    private final GoGame goGame;

    public GameData(GoGame goGame) {
        this.goGame = goGame;
    }

    public GoGame getGoGame() {
        return goGame;
    }

    @Override
    public String toString() {
        return "GameData{" +
                "goGame=" + goGame +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameData gameData = (GameData) o;

        return getGoGame().equals(gameData.getGoGame());
    }

    @Override
    public int hashCode() {
        return getGoGame().hashCode();
    }
}
