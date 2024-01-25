package com.goboardgame;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GoGame implements Serializable {
    private final int boardSize;
    private Stone[][] board;
    private Stone.StoneColor currentPlayer;
    private Set<Point> blackStones;
    private Set<Point> whiteStones;

    public GoGame(int boardSize) {
        this.boardSize = boardSize;
        this.board = new Stone[boardSize][boardSize];
        this.currentPlayer = Stone.StoneColor.BLACK; // Black starts first
        blackStones = new HashSet<>();
        whiteStones = new HashSet<>();
    }

    public boolean placeStone(int x, int y) {
        if (isValidMove(x, y)) {
            Stone newStone = new Stone(currentPlayer, x, y);
            board[x][y] = newStone;
            if (currentPlayer == Stone.StoneColor.BLACK) {
                blackStones.add(new Point(x, y));
            } else {
                whiteStones.add(new Point(x, y));
            }
            removeCapturedStones(x, y);
            togglePlayer();
            return true;
        }
        return false;
    }

    private void removeCapturedStones(int x, int y) {
        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];
            if (isOnBoard(adjacentX, adjacentY) && board[adjacentX][adjacentY] != null
                    && board[adjacentX][adjacentY].getColor() != currentPlayer) {
                if (!hasLiberties(adjacentX, adjacentY)) {
                    removeGroup(adjacentX, adjacentY);
                }
            }
        }
    }

    private boolean hasLiberties(int x, int y) {
        Stone.StoneColor color = board[x][y].getColor();
        Set<Point> stonesToCheck = (color == Stone.StoneColor.BLACK) ? blackStones : whiteStones;

        for (Point stone : stonesToCheck) {
            if (isAdjacent(x, y, stone.x, stone.y) && isLiberty(stone.x, stone.y)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAdjacent(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1;
    }

    private boolean isLiberty(int x, int y) {
        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];
            if (isOnBoard(adjacentX, adjacentY) && board[adjacentX][adjacentY] == null) {
                return true;
            }
        }
        return false;
    }

    private void removeGroup(int x, int y) {
        Stone.StoneColor color = board[x][y].getColor();
        Set<Point> stonesToRemove = (color == Stone.StoneColor.BLACK) ? blackStones : whiteStones;

        removeGroupRecursive(x, y, color, stonesToRemove);
    }

    private void removeGroupRecursive(int x, int y, Stone.StoneColor color, Set<Point> stonesToRemove) {
        if (!isOnBoard(x, y) || board[x][y] == null || board[x][y].getColor() != color) {
            return;
        }

        board[x][y] = null;
        stonesToRemove.remove(new Point(x, y));

        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];
            removeGroupRecursive(adjacentX, adjacentY, color, stonesToRemove);
        }
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

    private boolean isOnBoard(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public Stone[][] getBoard() {
        return board;
    }

    public Stone.StoneColor getCurrentPlayer() {
        return currentPlayer;
    }
}

class Point {
    int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
