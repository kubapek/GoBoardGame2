package com.goboardgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Main extends Application {
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private boolean isMyTurn = false;
    private boolean gameOver = false;
    private Stone.StoneColor selectedStoneColor = Stone.StoneColor.BLACK; // Początkowy kolor kamienia
    private GameBoard gameBoard;

    @Override
    public void start(Stage primaryStage) {
        connectToServer(primaryStage);
    }

    private void connectToServer(Stage primaryStage) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 8000);
                toServer = new ObjectOutputStream(socket.getOutputStream());
                fromServer = new ObjectInputStream(socket.getInputStream());

                GameData initialGameData = (GameData) fromServer.readObject();
                GoGame goGame = initialGameData.getGoGame();

                Platform.runLater(() -> {
                    createGameUI(primaryStage, goGame);
                });

                listenForUpdates();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void listenForUpdates() {
        new Thread(() -> {
            while (true) {
                try {
                    GameData gameData = (GameData) fromServer.readObject();
                    GoGame goGame = gameData.getGoGame();

                    Platform.runLater(() -> {
                        updateGameBoard(goGame);
                    });
                } catch (SocketException e) {
                    System.out.println("Zakończono grę");
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    public void updateGameBoard(GoGame goGame) {
        Platform.runLater(() -> {
            gameBoard.updateGameBoard();

            if (isMyTurn && !gameOver) {
                gameBoard.highlightSelectedStone(selectedStoneColor);
            }
        });
    }

    private void createGameUI(Stage primaryStage, GoGame goGame) {
        gameBoard = new GameBoard(goGame, toServer, this);
        StackPane root = new StackPane();
        root.getChildren().add(gameBoard.createContent());
        Scene scene = new Scene(root, 760, 760);
        primaryStage.setTitle("Gra w Go");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Stone.StoneColor getSelectedStoneColor() {
        return selectedStoneColor;
    }

    public void setSelectedStoneColor(Stone.StoneColor selectedStoneColor) {
        this.selectedStoneColor = selectedStoneColor;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
