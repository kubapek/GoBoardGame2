package com.goboardgame;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GoGame implements Serializable {
    private final int boardSize;
    private Stone[][] board;
    private Stone[][] previousBoardBlack;
    private Stone[][] previousBoardWhite;
    private Stone.StoneColor currentPlayer;
    private int playersCount = 0;
    private int player1Score;
    private int player2Score;
    private boolean gameEnded;

    public GoGame(int boardSize) {
        this.boardSize = boardSize;
        this.board = new Stone[boardSize][boardSize];
        this.previousBoardBlack = new Stone[boardSize][boardSize];
        this.previousBoardWhite = new Stone[boardSize][boardSize];
        this.currentPlayer = Stone.StoneColor.BLACK;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public boolean placeStone(int x, int y, Stone.StoneColor color) {
        if (isValidMove(x, y) && !isKo(x, y, color)) {
            Stone newStone = new Stone(color, x, y);
            board[x][y] = newStone;

            removeCapturedStones(x, y);

            if (color == Stone.StoneColor.BLACK) {
                previousBoardBlack = deepCopyBoard(board);
            } else {
                previousBoardWhite = deepCopyBoard(board);
            }

            boolean isSuicidal = !hasLiberties(x, y);

            if (isSuicidal && !hasLiberties(x, y)) {
                board[x][y] = null;
                return false;
            }

            togglePlayer();
            return true;
        }
        return false;
    }

    public boolean isKo(int x, int y, Stone.StoneColor color) {
        Stone[][] tempBoard = deepCopyBoard(this.board);
        tempBoard[x][y] = new Stone(color, x, y);

        simulateRemovalOfStones(tempBoard, x, y, color);

        Stone[][] previousBoard = (color == Stone.StoneColor.BLACK) ? previousBoardBlack : previousBoardWhite;
        return areBoardsEqual(tempBoard, previousBoard);
    }

    void simulateRemovalOfStones(Stone[][] board, int x, int y, Stone.StoneColor color) {
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

    boolean hasLiberties(Stone[][] board, Set<Point> group) {
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

    public void countTerritory() {
        boolean[][] visited = new boolean[boardSize][boardSize];
        player1Score = 0;
        player2Score = 0;

        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (!visited[x][y] && board[x][y] == null) {
                    Set<Point> territory = new HashSet<>();
                    Set<Stone.StoneColor> boundaryColors = new HashSet<>();
                    analyzeTerritory(x, y, visited, territory, boundaryColors);

                    if (boundaryColors.size() == 1) { // Terytorium należy do jednego gracza
                        Stone.StoneColor owner = boundaryColors.iterator().next();
                        int territoryPoints = territory.size();
                        if (owner == Stone.StoneColor.BLACK) {
                            player1Score += territoryPoints;
                        } else {
                            player2Score += territoryPoints;
                        }
                    }
                }
            }
        }
    }

    private void analyzeTerritory(int x, int y, boolean[][] visited, Set<Point> territory, Set<Stone.StoneColor> boundaryColors) {
        if (!isOnBoard(x, y) || visited[x][y]) {
            return;
        }

        visited[x][y] = true;

        if (board[x][y] == null) {
            territory.add(new Point(x, y));
            for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int adjacentX = x + direction[0];
                int adjacentY = y + direction[1];
                if (isOnBoard(adjacentX, adjacentY)) {
                    if (board[adjacentX][adjacentY] == null) {
                        analyzeTerritory(adjacentX, adjacentY, visited, territory, boundaryColors);
                    } else {
                        boundaryColors.add(board[adjacentX][adjacentY].getColor());
                    }
                }
            }
        }
    }


    boolean areBoardsEqual(Stone[][] board1, Stone[][] board2) {
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

    Stone[][] deepCopyBoard(Stone[][] boardToCopy) {
        Stone[][] newBoard = new Stone[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                newBoard[i][j] = boardToCopy[i][j] != null ? new Stone(boardToCopy[i][j].getColor(), i, j) : null;
            }
        }
        return newBoard;
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
            player1Score += capturedGroup.size();
        } else {
            player2Score += capturedGroup.size();
        }
    }

    Set<Point> findGroup(int x, int y) {
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

    boolean hasLiberties(int x, int y) {
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

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded() {
        this.gameEnded = true;
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
