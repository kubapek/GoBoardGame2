package com.goboardgame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GoGame implements Serializable {
    private final int boardSize;
    private Stone[][] board;
    private Stone.StoneColor currentPlayer;

    public GoGame(int boardSize) {
        this.boardSize = boardSize;
        this.board = new Stone[boardSize][boardSize];
        this.currentPlayer = Stone.StoneColor.BLACK; // Black starts first
    }


    public GameData getCurrentGameData() {
        return new GameData(this);
    }

    public boolean placeStone(int x, int y) {
        if (isValidMove(x, y)) {
            board[x][y] = new Stone(currentPlayer, x, y);
            togglePlayer();
            return true;
        }
        return false;
    }

    private boolean isValidMove(int x, int y) {
        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
            return false; // Out of bounds
        }
        return board[x][y] == null; // Valid if the spot is empty
    }

    private void togglePlayer() {
        currentPlayer = (currentPlayer == Stone.StoneColor.BLACK) ? Stone.StoneColor.WHITE : Stone.StoneColor.BLACK;
    }

    public Stone.StoneColor getCurrentPlayer() {
        return currentPlayer;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public Stone[][] getBoard() {
        return board;
    }

    // Additional methods can be implemented here, such as for checking captures, game over conditions, etc.
}
