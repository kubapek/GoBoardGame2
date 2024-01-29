package com.goboardgame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private GoGameServer goGameServer;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Stone.StoneColor playerColor;

    public ClientHandler(Socket clientSocket, GoGameServer goGameServer) {
        this.clientSocket = clientSocket;
        this.goGameServer = goGameServer;

        if (goGameServer.getGoGame().getPlayersCount() == 0) {
            playerColor = Stone.StoneColor.BLACK; // Pierwszy gracz dostaje kolor czarny
        } else if (goGameServer.getGoGame().getPlayersCount() == 1) {
            playerColor = Stone.StoneColor.WHITE; // Drugi gracz dostaje kolor biały
        }

        // Zwiększ liczbę graczy na planszy
        goGameServer.getGoGame().incrementPlayersCount();

        try {
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendInitialGameData();
    }

    private void sendInitialGameData() {
        try {
            GameData initialGameData = new GameData(goGameServer.getGoGame());
            outputStream.writeObject(initialGameData);
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
//                System.out.println("Waiting for data from client: " + clientSocket);
                Object receivedData = inputStream.readObject();
//                System.out.println("Received data from client: " + clientSocket + ", data: " + receivedData);
                if (receivedData instanceof MoveData moveData) {
                    goGameServer.handleMove(moveData, this);
                }
                if (receivedData instanceof PlayerToggleRequest) {
                    if(goGameServer.isLastMoveResignation()) {
                        GoGame EndGoGame = goGameServer.getGoGame();
                        EndGameData endGameData = new EndGameData(EndGoGame);
                        goGameServer.broadcastEndGameData(endGameData);
                    }
                    else if(playerColor == goGameServer.getGoGame().getCurrentPlayer()) {
                        goGameServer.getGoGame().togglePlayer();
                        goGameServer.setLastMoveResignation(true);
                    }
                }
                if (receivedData instanceof SurrenderRequest) {
                   if(playerColor == goGameServer.getGoGame().getCurrentPlayer()) {
                       goGameServer.getGoGame().setGameEnded();
                       goGameServer.broadcastSurrenderData(this);
                       String kolor = playerColor == Stone.StoneColor.BLACK ? "czarnym" : "bialym";
                       System.out.println("wysłane info o poddaniu sie przez gracza o kolorze " + kolor);
                   }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error or disconnection with client " + clientSocket + ": " + e.getMessage());
            goGameServer.removeClient(this);
        }
    }

    public void sendGameData(GameData gameData) throws IOException {
//        System.out.println("Sending game data to client: " + clientSocket);
        outputStream.writeObject(gameData);
        outputStream.reset();
    }

    public void sendWinnerInfo(WinnerInfo winnerInfo) throws IOException {
        System.out.println("Sending winner data to client: " + clientSocket);
        outputStream.writeObject(winnerInfo);
        outputStream.reset();
    }

    public void sendEndGameData(EndGameData endGameData) throws IOException {
        //System.out.println("Sending endGameData to client: " + clientSocket);
        outputStream.writeObject(endGameData);
        outputStream.reset();
    }

    public Stone.StoneColor getPlayerColor() {
        return playerColor;
    }
}
