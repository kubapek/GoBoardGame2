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
                    Object receivedData = fromServer.readObject();
                    if(receivedData instanceof WinnerInfo winnerInfo)
                        displayWinnerDialog(winnerInfo.getWinnerMessage());
                    if(receivedData instanceof EndGameData endGameData)
                        displayPointsDialog(endGameData.getEndGoGame());
                    if(receivedData instanceof GameData gameData) {
                        System.out.println(gameData);
                        GoGame goGame = gameData.getGoGame();
                        refreshGameUI(goGame, primaryStage);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error or disconnection from server: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }


    private void createGameUI(Stage primaryStage, GoGame goGame) {
        gameBoard = new GameBoard(goGame, toServer, this);

        VBox scoreBoard = new VBox(10);
        Label player1ScoreLabel = new Label("Czarne: " + goGame.getPlayer1Score());
        Label player2ScoreLabel = new Label("Białe: " + goGame.getPlayer2Score());

        Button resignButton = new Button("Zrezygnuj");
        resignButton.setOnAction(e -> {
            try {
                toServer.writeObject(new PlayerToggleRequest());
                toServer.flush();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        });

        Button surrenderButton = new Button("Poddaj grę");
        surrenderButton.setOnAction(e -> {
            try {
                toServer.writeObject(new SurrenderRequest());
                toServer.flush();
                System.out.println("gracz poddał sie");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        });

        scoreBoard.getChildren().addAll(player1ScoreLabel, player2ScoreLabel, resignButton, surrenderButton);

        BorderPane root = new BorderPane();
        root.setCenter(gameBoard.createContent());
        root.setLeft(scoreBoard);
        root.setBackground(new Background(new BackgroundFill(Color.BURLYWOOD,null,null)));

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void refreshGameUI(GoGame goGame, Stage primaryStage) {
        Platform.runLater(() -> createGameUI(primaryStage, goGame));
    }

    private void displayWinnerDialog(String winnerMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Koniec gry");
            alert.setHeaderText(null);
            alert.setContentText(winnerMessage);
            alert.setOnHidden(event -> {
                Platform.exit();
            });
            alert.showAndWait();
        });
    }

    private void displayPointsDialog(GoGame goGame){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Koniec gry");
            alert.setHeaderText(null);
            alert.setContentText("Podsumowanie punktów zdobytych przez graczy: \n" + "Czarne: " + goGame.getPlayer1Score()+"\nBiałe: " + goGame.getPlayer2Score());
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