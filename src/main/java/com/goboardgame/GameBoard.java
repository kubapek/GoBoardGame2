package com.goboardgame;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class GameBoard extends Pane {
    public static final int TILE_SIZE = 40;
    private GoGame goGame;
    private ObjectOutputStream toServer;
    private Main main;

    public GameBoard(GoGame goGame, ObjectOutputStream toServer, Main main) {
        this.goGame = goGame;
        this.toServer = toServer;
        this.main = main;
    }

    public Pane createContent() {
        setPrefSize((goGame.getBoardSize() - 1) * TILE_SIZE, (goGame.getBoardSize() - 1) * TILE_SIZE);

        for (int i = 0; i < goGame.getBoardSize(); i++) {
            Line hLine = new Line(0, i * TILE_SIZE, (goGame.getBoardSize() - 1) * TILE_SIZE, i * TILE_SIZE);
            Line vLine = new Line(i * TILE_SIZE, 0, i * TILE_SIZE, (goGame.getBoardSize() - 1) * TILE_SIZE);
            getChildren().addAll(hLine, vLine);
        }

        for (int y = 0; y < goGame.getBoardSize(); y++) {
            for (int x = 0; x < goGame.getBoardSize(); x++) {
                Circle interactionPoint = new Circle(TILE_SIZE / 2 - 2, Color.TRANSPARENT);
                interactionPoint.setCenterX(x * TILE_SIZE);
                interactionPoint.setCenterY(y * TILE_SIZE);

                int finalX = x;
                int finalY = y;
                interactionPoint.setOnMouseClicked(e -> handleStonePlacement(finalX, finalY));

                getChildren().add(interactionPoint);
            }
        }

        return this;
    }

    private void handleStonePlacement(int x, int y) {
        System.out.println("Kliknięcie myszą na pozycji x=" + x + ", y=" + y);
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


    public void updateGameBoard() {
        getChildren().clear();

        for (int i = 0; i < goGame.getBoardSize(); i++) {
            Line hLine = new Line(0, i * TILE_SIZE, (goGame.getBoardSize() - 1) * TILE_SIZE, i * TILE_SIZE);
            Line vLine = new Line(i * TILE_SIZE, 0, i * TILE_SIZE, (goGame.getBoardSize() - 1) * TILE_SIZE);
            getChildren().addAll(hLine, vLine);
        }

        for (int y = 0; y < goGame.getBoardSize(); y++) {
            for (int x = 0; x < goGame.getBoardSize(); x++) {
                if (goGame.getBoard()[x][y] != null) {
                    Stone stone = goGame.getBoard()[x][y];
                    Circle circle = new Circle(TILE_SIZE / 2 - 2);
                    circle.setFill(stone.getColor() == Stone.StoneColor.BLACK ? Color.BLACK : Color.WHITE);
                    circle.setCenterX(x * TILE_SIZE);
                    circle.setCenterY(y * TILE_SIZE);
                    getChildren().add(circle);
                }
            }
        }
    }

    public void highlightSelectedStone(Stone.StoneColor selectedStoneColor) {
        if (selectedStoneColor == Stone.StoneColor.BLACK) {
            Circle selectedStone = new Circle(TILE_SIZE / 2 - 2);
            selectedStone.setFill(Color.BLACK);
            selectedStone.setCenterX(700); //
            selectedStone.setCenterY(700); //
            getChildren().add(selectedStone);
        } else {
            Circle selectedStone = new Circle(TILE_SIZE / 2 - 2);
            selectedStone.setFill(Color.WHITE);
            selectedStone.setCenterX(700);
            selectedStone.setCenterY(700);
            getChildren().add(selectedStone);
        }
    }
}
