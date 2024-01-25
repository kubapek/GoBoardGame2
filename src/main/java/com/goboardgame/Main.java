package com.goboardgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Socket socket = new Socket("localhost", 8000);
            System.out.println("Połączono z serwerem");

            GoGame goGame = new GoGame(19);
            GameBoard board = new GameBoard(goGame, socket);

            StackPane root = new StackPane();
            root.getChildren().add(board.createContent());

            Scene scene = new Scene(root, 760, 760);

            primaryStage.setTitle("Gra w Go");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
