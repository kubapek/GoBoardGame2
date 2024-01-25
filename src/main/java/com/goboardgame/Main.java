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
    private GoGame goGame;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    @Override
    public void start(Stage primaryStage) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 8000);
                toServer = new ObjectOutputStream(socket.getOutputStream());
                fromServer = new ObjectInputStream(socket.getInputStream());

                // Oczekiwanie na otrzymanie początkowego stanu gry
                GameData initialGameData = (GameData) fromServer.readObject();
                goGame = initialGameData.getGoGame();

                // Tworzenie interfejsu użytkownika w wątku aplikacji JavaFX
                Platform.runLater(() -> {
                    GameBoard board = new GameBoard(goGame, socket);
                    StackPane root = new StackPane();
                    root.getChildren().add(board.createContent());
                    Scene scene = new Scene(root, 760, 760);
                    primaryStage.setTitle("Gra w Go");
                    primaryStage.setScene(scene);
                    primaryStage.show();
                });

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
