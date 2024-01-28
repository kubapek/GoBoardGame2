package com.goboardgame;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GoGame implements Serializable {
    private final int boardSize;
    private Stone[][] board;
    private Stone[][] previousBoardBlack;
    private Stone[][] previousBoardWhite;
    private Stone.StoneColor currentPlayer;
    private Set<Point> blackStones;
    private Set<Point> whiteStones;
    private int playersCount = 0;

    public GoGame(int boardSize) {
        this.boardSize = boardSize;
        this.board = new Stone[boardSize][boardSize];
        this.previousBoardBlack = new Stone[boardSize][boardSize];
        this.previousBoardWhite = new Stone[boardSize][boardSize];
        this.currentPlayer = Stone.StoneColor.BLACK;
        blackStones = new HashSet<>();
        whiteStones = new HashSet<>();
    }

    public boolean placeStone(int x, int y, Stone.StoneColor color) {
        if (isValidMove(x, y) && !isKo(x,y,color)) {
            Stone newStone = new Stone(color, x, y);
            board[x][y] = newStone;

            removeCapturedStones(x, y);

            if (color == Stone.StoneColor.BLACK) {
                previousBoardBlack = deepCopyBoard(board);
                blackStones.add(new Point(x, y));
            } else {
                previousBoardWhite = deepCopyBoard(board);
                whiteStones.add(new Point(x, y));
            }

            boolean isSuicidal = !hasLiberties(x, y);

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
            return true;
        }
        return false;
    }

    public boolean isKo(int x, int y, Stone.StoneColor color) {
        // Create a temporary copy of the current board and place the stone
        Stone[][] tempBoard = deepCopyBoard(this.board);
        tempBoard[x][y] = new Stone(color, x, y);

        // Simulate removal of opponent's stones on the temporary board
        simulateRemovalOfStones(tempBoard, x, y, color);

        // Get the appropriate previous board state
        Stone[][] previousBoard = (color == Stone.StoneColor.BLACK) ? previousBoardBlack : previousBoardWhite;

        // Compare the temporary board with the previous board state
        return areBoardsEqual(tempBoard, previousBoard);
    }
    private void simulateRemovalOfStones(Stone[][] board, int x, int y, Stone.StoneColor color) {
        Stone.StoneColor opponentColor = (color == Stone.StoneColor.BLACK) ? Stone.StoneColor.WHITE : Stone.StoneColor.BLACK;

        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];

            if (isOnBoard(adjacentX, adjacentY) && board[adjacentX][adjacentY] != null
                    && board[adjacentX][adjacentY].getColor() == opponentColor) {
                Set<Point> group = findGroup(board, adjacentX, adjacentY, opponentColor);
                if (!hasLiberties(board, group)) {
                    for (Point stone : group) {
                        board[stone.getX()][stone.getY()] = null;
                    }
                }
            }
        }
    }

    private Set<Point> findGroup(Stone[][] board, int x, int y, Stone.StoneColor color) {
        Set<Point> group = new HashSet<>();
        findGroupRecursive(board, x, y, color, group);
        return group;
    }

    private void findGroupRecursive(Stone[][] board, int x, int y, Stone.StoneColor color, Set<Point> group) {
        if (!isOnBoard(x, y) || board[x][y] == null || board[x][y].getColor() != color || group.contains(new Point(x, y))) {
            return;
        }

        group.add(new Point(x, y));

        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];
            findGroupRecursive(board, adjacentX, adjacentY, color, group);
        }
    }

    private boolean hasLiberties(Stone[][] board, Set<Point> group) {
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

    private boolean areBoardsEqual(Stone[][] board1, Stone[][] board2) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board1[i][j] != null && board2[i][j] != null) {
                    if (board1[i][j].getColor() != board2[i][j].getColor()) {
                        return false;
                    }
                } else if (board1[i][j] != board2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }



    private Stone[][] deepCopyBoard(Stone[][] boardToCopy) {
        Stone[][] newBoard = new Stone[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                newBoard[i][j] = boardToCopy[i][j] != null ? new Stone(boardToCopy[i][j].getColor(), i, j) : null;
            }
        }
        return newBoard;
    }



    private void removeCapturedStones(int x, int y) {
        for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int adjacentX = x + direction[0];
            int adjacentY = y + direction[1];
            if (isOnBoard(adjacentX, adjacentY) && board[adjacentX][adjacentY] != null
                    && board[adjacentX][adjacentY].getColor() != currentPlayer) {
                Set<Point> group = findGroup(adjacentX, adjacentY);
                if (!hasLiberties(group)) {
                    removeGroup(group);
                }
            }
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
