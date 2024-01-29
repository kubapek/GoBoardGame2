package com.goboardgame;

import java.io.Serializable;

public class WinnerInfo implements Serializable {
    private String winnerMessage;

    public WinnerInfo(String winnerMessage) {
        this.winnerMessage = winnerMessage;
    }

    public String getWinnerMessage() {
        return winnerMessage;
    }
}