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
                    System.out.println("Received game data: " + gameData);
                    GoGame goGame = gameData.getGoGame();
                    Platform.runLater(() -> {
                        gameBoard.setGoGame(goGame); // Aktualizuje stan gry w gameBoard
                        gameBoard.updateGameBoard(); // Rysuje ponownie planszę
                    });
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


    public static void main(String[] args) {
        launch(args);
    }

}