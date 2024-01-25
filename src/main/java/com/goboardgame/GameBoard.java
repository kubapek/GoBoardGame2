package com.goboardgame;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.net.Socket;

public class GameBoard {
    private static final int TILE_SIZE = 40;
    private static final int INTERACTION_RADIUS = 10; // Promień punktu interakcyjnego
    private GoGame goGame;
    private Pane root;

    private Socket socket;

    public GameBoard(GoGame goGame, Socket socket) {
        this.goGame = goGame;
        this.root = new Pane();
        this.socket = socket;
    }

    public Pane createContent() {
        root.setPrefSize((goGame.getBoardSize() - 1) * TILE_SIZE, (goGame.getBoardSize() - 1) * TILE_SIZE);

        // Draw grid lines
        for (int i = 0; i < goGame.getBoardSize(); i++) {
            Line hLine = new Line(0, i * TILE_SIZE, (goGame.getBoardSize() - 1) * TILE_SIZE, i * TILE_SIZE);
            Line vLine = new Line(i * TILE_SIZE, 0, i * TILE_SIZE, (goGame.getBoardSize() - 1) * TILE_SIZE);
            root.getChildren().addAll(hLine, vLine);
        }

        // Add interaction points
        for (int y = 0; y < goGame.getBoardSize(); y++) {
            for (int x = 0; x < goGame.getBoardSize(); x++) {
                final int finalX = x;
                final int finalY = y;

                Circle interactionPoint = new Circle(INTERACTION_RADIUS, Color.TRANSPARENT);
                interactionPoint.setCenterX(finalX * TILE_SIZE);
                interactionPoint.setCenterY(finalY * TILE_SIZE);
                interactionPoint.setOnMouseClicked(e -> placeStone(finalX, finalY, root));

                root.getChildren().add(interactionPoint);
            }
        }

        return root;
    }

    private void placeStone(int x, int y, Pane root) {
        if (goGame.placeStone(x, y)) {
            Stone stone = new Stone(goGame.getCurrentPlayer(), x, y);
            Circle circle = new Circle(TILE_SIZE / 2 - 2);
            circle.setFill(stone.getColor() == Stone.StoneColor.BLACK ? Color.BLACK : Color.WHITE);
            circle.setCenterX(x * TILE_SIZE);
            circle.setCenterY(y * TILE_SIZE);
            root.getChildren().add(circle);
        }
    }
}
