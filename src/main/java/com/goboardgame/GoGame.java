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
    private int playersCount = 0;

    public GoGame(int boardSize) {
        this.boardSize = boardSize;
        this.board = new Stone[boardSize][boardSize];
        this.currentPlayer = Stone.StoneColor.BLACK;
        blackStones = new HashSet<>();
        whiteStones = new HashSet<>();
    }

    public boolean placeStone(int x, int y, Stone.StoneColor color) {
        if (isValidMove(x, y)) {
            Stone newStone = new Stone(color, x, y);
            board[x][y] = newStone;
            if (color == Stone.StoneColor.BLACK) {
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
        Set<Point> visited = new HashSet<>();
        return checkLibertiesRecursive(x, y, color, visited);
    }

    private boolean checkLibertiesRecursive(int x, int y, Stone.StoneColor color, Set<Point> visited) {
        if (!isOnBoard(x, y) || visited.contains(new Point(x, y))) {
            return false;
        }

        if (board[x][y] == null) {
            return true;
        }

        if (board[x][y].getColor() != color) {
            return false;
        }

        visited.add(new Point(x, y));

        // Sprawdź wszystkie kierunki
        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];
            if (checkLibertiesRecursive(adjacentX, adjacentY, color, visited)) {
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
            return false;
        }
        return board[x][y] == null;
    }

    public void togglePlayer() {
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

    public int getPlayersCount() {
        return playersCount;
    }

    public void incrementPlayersCount() {
        playersCount += 1;
    }

    public Set<Point> getBlackStones() {
        return blackStones;
    }

    public Set<Point> getWhiteStones() {
        return whiteStones;
    }

    @Override
    public String toString() {
        return "GoGame{" +
                "currentPlayer=" + currentPlayer +
                ", blackStones=" + blackStones +
                ", whiteStones=" + whiteStones +
                '}';
    }
}

class Point implements Serializable {

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
