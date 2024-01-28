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
    private int player1Score;
    private int player2Score;

    public GoGame(int boardSize) {
        this.boardSize = boardSize;
        this.board = new Stone[boardSize][boardSize];
        this.currentPlayer = Stone.StoneColor.BLACK;
        blackStones = new HashSet<>();
        whiteStones = new HashSet<>();
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    private void updateScores() {
        player1Score = countStonesInGroup(blackStones);
        player2Score = countStonesInGroup(whiteStones);
    }

    private int countStonesInGroup(Set<Point> group) {
        return group.size();
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

            boolean isSuicidal = !hasLiberties(x, y);
            removeCapturedStones(x, y);

            if (isSuicidal && !hasLiberties(x, y)) {
                board[x][y] = null;
                if (color == Stone.StoneColor.BLACK) {
                    blackStones.remove(new Point(x, y));
                } else {
                    whiteStones.remove(new Point(x, y));
                }
                return false;
            }

            togglePlayer();
            updateScores();
            return true;
        }
        return false;
    }


    private void removeCapturedStones(int x, int y) {
        Set<Point> capturedGroup = new HashSet<>();
        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];
            if (isOnBoard(adjacentX, adjacentY) && board[adjacentX][adjacentY] != null
                    && board[adjacentX][adjacentY].getColor() != currentPlayer) {
                Set<Point> group = findGroup(adjacentX, adjacentY);
                if (!hasLiberties(group)) {
                    capturedGroup.addAll(group);
                    removeGroup(group);
                }
            }
        }
        if (currentPlayer == Stone.StoneColor.BLACK) {
            whiteStones.removeAll(capturedGroup);
            player2Score -= capturedGroup.size();
        } else {
            blackStones.removeAll(capturedGroup);
            player1Score -= capturedGroup.size();
        }
    }

    private Set<Point> findGroup(int x, int y) {
        Set<Point> group = new HashSet<>();
        Stone.StoneColor color = board[x][y].getColor();
        findGroupRecursive(x, y, color, group);
        return group;
    }

    private void findGroupRecursive(int x, int y, Stone.StoneColor color, Set<Point> group) {
        if (!isOnBoard(x, y) || board[x][y] == null || board[x][y].getColor() != color || group.contains(new Point(x, y))) {
            return;
        }

        group.add(new Point(x, y));

        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];
            findGroupRecursive(adjacentX, adjacentY, color, group);
        }
    }

    private boolean hasLiberties(int x, int y) {
        if (!isOnBoard(x, y) || board[x][y] == null) {
            return false;
        }

        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];
            if (isOnBoard(adjacentX, adjacentY) && board[adjacentX][adjacentY] == null) {
                return true;
            }
        }

        return false;
    }


    private boolean hasLiberties(Set<Point> group) {
        for (Point stone : group) {
            int x = stone.getX();
            int y = stone.getY();
            for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int adjacentX = x + direction[0];
                int adjacentY = y + direction[1];
                if (isOnBoard(adjacentX, adjacentY) && board[adjacentX][adjacentY] == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeGroup(Set<Point> group) {
        for (Point stone : group) {
            int x = stone.getX();
            int y = stone.getY();
            board[x][y] = null;
            if (currentPlayer == Stone.StoneColor.BLACK) {
                blackStones.remove(stone);
            } else {
                whiteStones.remove(stone);
            }
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
