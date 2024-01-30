package com.goboardgame.dto;

import java.io.Serializable;

public class WinnerInfo implements Serializable {
    private final String winnerMessage;

    public WinnerInfo(String winnerMessage) {
        this.winnerMessage = winnerMessage;
    }

    public String getWinnerMessage() {
        return winnerMessage;
    }
}