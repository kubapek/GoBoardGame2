package com.goboardgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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
        StackPane root = new StackPane();
        root.getChildren().add(gameBoard.createContent());
        Scene scene = new Scene(root, 760, 760);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void refreshGameUI(GoGame goGame, Stage primaryStage) {
        Platform.runLater(() -> {
            gameBoard = new GameBoard(goGame, toServer, this);
            StackPane root = new StackPane();
            root.getChildren().add(gameBoard.createContent());
            Scene scene = new Scene(root, 760, 760);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }



    public static void main(String[] args) {
        launch(args);
    }

}