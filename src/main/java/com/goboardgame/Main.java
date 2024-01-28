package com.goboardgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Main extends Application {
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private GameBoard gameBoard;

    @Override
    public void start(Stage primaryStage) {
        connectToServer(primaryStage);
    }

    private void connectToServer(Stage primaryStage) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 8000);
                toServer = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                fromServer = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                toServer.flush();

                GameData initialGameData = (GameData) fromServer.readObject();
                GoGame goGame = initialGameData.getGoGame();

                Platform.runLater(() -> {
                    createGameUI(primaryStage, goGame);
                });

                listenForUpdates(primaryStage);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void listenForUpdates(Stage primaryStage) {
        new Thread(() -> {
            while (true) {
                try {
                    GameData gameData = (GameData) fromServer.readObject();
                    System.out.println(gameData);
                    GoGame goGame = gameData.getGoGame();
                    refreshGameUI(goGame, primaryStage); // Aktualizuje UI na podstawie nowych danych
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error or disconnection from server: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }



    public void updateGameBoard(GoGame goGame) {
        Platform.runLater(() -> {
            gameBoard.setGoGame(goGame); // Aktualizacja stanu gry
            // Nie trzeba wywoływać tutaj updateGameBoard(), jeśli jest ona wywoływana w setGoGame
        });
    }


    private void createGameUI(Stage primaryStage, GoGame goGame) {
        gameBoard = new GameBoard(goGame, toServer, this);

        VBox scoreBoard = new VBox();
        Label player1ScoreLabel = new Label("Gracz 1: " /*+ goGame.getPlayer1Score()*/);
        Label player2ScoreLabel = new Label("Gracz 2: " /*+ goGame.getPlayer2Score()*/);
        scoreBoard.getChildren().addAll(player1ScoreLabel, player2ScoreLabel);

        Button resignButton = new Button("Zrezygnuj");
        resignButton.setOnAction(e -> {

        });

        HBox rightPane = new HBox(10);
        rightPane.getChildren().addAll(scoreBoard, resignButton);
        rightPane.setAlignment(Pos.CENTER_LEFT);

        BorderPane root = new BorderPane();
        root.setCenter(gameBoard.createContent());
        root.setLeft(rightPane);

        Scene scene = new Scene(root, 960, 760);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void refreshGameUI(GoGame goGame, Stage primaryStage) {
        Platform.runLater(() -> {
            gameBoard = new GameBoard(goGame, toServer, this);

            VBox scoreBoard = new VBox();
            Label player1ScoreLabel = new Label("Gracz 1: " /*+ goGame.getPlayer1Score()*/);
            Label player2ScoreLabel = new Label("Gracz 2: " /*+ goGame.getPlayer2Score()*/);
            scoreBoard.getChildren().addAll(player1ScoreLabel, player2ScoreLabel);

            Button resignButton = new Button("Zrezygnuj");
            resignButton.setOnAction(e -> {
                try {
                    toServer.writeObject(new PlayerToggleRequest());
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            });

            HBox rightPane = new HBox(10);
            rightPane.getChildren().addAll(scoreBoard, resignButton);
            rightPane.setAlignment(Pos.CENTER_LEFT);

            BorderPane root = new BorderPane();
            root.setCenter(gameBoard.createContent());
            root.setLeft(rightPane);

            Scene scene = new Scene(root, 960, 760);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}