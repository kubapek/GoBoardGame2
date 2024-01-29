package com.goboardgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Main extends Application {
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private GameBoard gameBoard;
    private boolean player1Surrendered = false;
    private boolean player2Surrendered = false;
    private boolean gameEnded = false;

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
                    refreshGameUI(goGame, primaryStage);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error or disconnection from server: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }


    private void createGameUI(Stage primaryStage, GoGame goGame) {
        gameBoard = new GameBoard(goGame, toServer, this);

        VBox scoreBoard = new VBox();
        Label player1ScoreLabel = new Label("Gracz 1: " + goGame.getPlayer1Score());
        Label player2ScoreLabel = new Label("Gracz 2: " + goGame.getPlayer2Score());
        scoreBoard.getChildren().addAll(player1ScoreLabel, player2ScoreLabel);

        Button resignButton = new Button("Zrezygnuj");
        resignButton.setOnAction(e -> {

        });

        Button surrenderButton = new Button("Poddaj grę");
        surrenderButton.setOnAction(e -> {

        });

        HBox poddanie = new HBox(10);
        poddanie.getChildren().addAll(surrenderButton);
        poddanie.setAlignment(Pos.BOTTOM_LEFT);

        HBox rightPane = new HBox(10);
        rightPane.getChildren().addAll(scoreBoard, resignButton);
        rightPane.setAlignment(Pos.CENTER_LEFT);

        BorderPane root = new BorderPane();
        root.setCenter(gameBoard.createContent());
        root.setLeft(rightPane);
        root.setBottom(poddanie);
        root.setBackground(new Background(new BackgroundFill(Color.BURLYWOOD,null,null)));

        Scene scene = new Scene(root, 960, 760);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void refreshGameUI(GoGame goGame, Stage primaryStage) {
        Platform.runLater(() -> {
            gameBoard = new GameBoard(goGame, toServer, this);

            VBox scoreBoard = new VBox();
            Label player1ScoreLabel = new Label("Gracz 1: " + goGame.getPlayer1Score());
            Label player2ScoreLabel = new Label("Gracz 2: " + goGame.getPlayer2Score());
            scoreBoard.getChildren().addAll(player1ScoreLabel, player2ScoreLabel);

            Button resignButton = new Button("Zrezygnuj");
            resignButton.setOnAction(e -> {

            });

            Button surrenderButton = new Button("Poddaj grę");
            surrenderButton.setOnAction(e -> {
                try {
                    toServer.writeObject(new SurrenderRequest());
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            });

            HBox poddanie = new HBox(10);
            poddanie.getChildren().addAll(surrenderButton);
            poddanie.setAlignment(Pos.CENTER_LEFT);

            HBox rightPane = new HBox(10);
            rightPane.getChildren().addAll(scoreBoard, resignButton);
            rightPane.setAlignment(Pos.CENTER_LEFT);

            BorderPane root = new BorderPane();
            root.setCenter(gameBoard.createContent());
            root.setLeft(rightPane);
            root.setBottom(poddanie);
            root.setBackground(new Background(new BackgroundFill(Color.BURLYWOOD, null, null)));

            Scene scene = new Scene(root, 960, 760);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    private void displayWinnerDialog(WinnerInfo winnerInfo) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Koniec gry");
            alert.setHeaderText(null);
            alert.setContentText(winnerInfo.getWinnerMessage());
            alert.setOnHidden(event -> {
                Platform.exit();
            });
            alert.showAndWait();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}