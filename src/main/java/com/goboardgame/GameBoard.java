package com.goboardgame;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class GameBoard extends Pane {
    public static final int TILE_SIZE = 40;
    private final GoGame goGame;
    private final ObjectOutputStream toServer;
    private static final int BOARD_MARGIN = 20;

    public GameBoard(GoGame goGame, ObjectOutputStream toServer) {
        this.goGame = goGame;
        this.toServer = toServer;
    }

    public Pane createContent() {
        drawGrid();
        drawStones();
        return this;
    }


    private void handleStonePlacement(int x, int y) {
        System.out.println("Handling stone placement at x=" + x + ", y=" + y);
        sendMoveToServer(x, y);
    }


    private void sendMoveToServer(int x, int y) {
        try {
            MoveData moveData = new MoveData(x, y);
            toServer.writeObject(moveData);
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawGrid() {
        getChildren().clear();

        for (int i = 0; i < goGame.getBoardSize(); i++) {
            Line hLine = new Line(BOARD_MARGIN, i * TILE_SIZE + BOARD_MARGIN, (goGame.getBoardSize() - 1) * TILE_SIZE + BOARD_MARGIN, i * TILE_SIZE + BOARD_MARGIN);
            Line vLine = new Line(i * TILE_SIZE + BOARD_MARGIN, BOARD_MARGIN, i * TILE_SIZE + BOARD_MARGIN, (goGame.getBoardSize() - 1) * TILE_SIZE + BOARD_MARGIN);
            getChildren().addAll(hLine, vLine);
        }

        for (int y = 0; y < goGame.getBoardSize(); y++) {
            for (int x = 0; x < goGame.getBoardSize(); x++) {
                Circle interactionPoint = new Circle(TILE_SIZE / 2 - 2, Color.TRANSPARENT);
                interactionPoint.setCenterX(x * TILE_SIZE + BOARD_MARGIN);
                interactionPoint.setCenterY(y * TILE_SIZE + BOARD_MARGIN);

                int finalX = x;
                int finalY = y;
                interactionPoint.setOnMouseClicked(e -> {
//                    System.out.println("Mouse clicked at: x=" + finalX + ", y=" + finalY);
                    handleStonePlacement(finalX, finalY);
                });

                getChildren().add(interactionPoint);
            }
        }
    }

    public void drawStones() {
        for (int y = 0; y < goGame.getBoardSize(); y++) {
            for (int x = 0; x < goGame.getBoardSize(); x++) {
                Stone stone = goGame.getBoard()[x][y];
                if (stone != null) {
                    Circle circle = new Circle(TILE_SIZE / 2 - 2);
                    circle.setFill(stone.getColor() == Stone.StoneColor.BLACK ? Color.BLACK : Color.WHITE);
                    circle.setCenterX(x * TILE_SIZE + BOARD_MARGIN);
                    circle.setCenterY(y * TILE_SIZE + BOARD_MARGIN);
                    getChildren().add(circle);
                }
            }
        }
    }
}
