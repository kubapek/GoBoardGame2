package com.goboardgame.server;

import com.goboardgame.GoGame;
import com.goboardgame.Stone;
import com.goboardgame.dto.EndGameData;
import com.goboardgame.dto.GameData;
import com.goboardgame.dto.MoveData;
import com.goboardgame.dto.WinnerInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GoGameServer {
    private static final int PORT = 8000;
    public Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());
    public GoGame goGame;
    private boolean lastMoveResignation = false;

    public GoGameServer() {
        this.goGame = new GoGame(9);
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("GoGame server is running on port " + PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket);
                    if(!goGame.isGameEnded()) {
                        ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                        clients.add(clientHandler);

                        new Thread(clientHandler).start();
                    }
                } catch (IOException e) {
                    System.out.println("Error connecting to a client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to start the server: " + e.getMessage());
        }
    }

    public void handleMove(MoveData moveData, ClientHandler sender) {
        int x = moveData.getX();
        int y = moveData.getY();
        Stone.StoneColor currentPlayer = goGame.getCurrentPlayer();

        if (sender.getPlayerColor() == currentPlayer) {
            if (goGame.placeStone(x, y, currentPlayer)) {
                System.out.println("Player " + currentPlayer + " placed a stone at position x=" + x + ", y=" + y);
                System.out.println(goGame);
                // Dodatkowe logowanie stanu gry
                logGameState();
                goGame.countTerritory();
                GameData updatedGameData = new GameData(goGame);
                broadcastGameData(updatedGameData);
                setLastMoveResignation(false);
            }
        }
    }

    public void broadcastSurrenderData(ClientHandler sender) {
        String winnerMessage = sender.getPlayerColor() == Stone.StoneColor.BLACK ? "Białe wygrały" : "Czarne wygrały";
        WinnerInfo winnerInfo = new WinnerInfo(winnerMessage);
        for (ClientHandler client : clients) {
            try {
//                System.out.println("Sending game data to client: " + client);
                client.sendWinnerInfo(winnerInfo);
            } catch (IOException e) {
//                System.out.println("Error sending data to client " + client + ": " + e.getMessage());
            }
        }
    }

    public void broadcastEndGameData(EndGameData endGameData){
        for (ClientHandler client : clients) {
            try {
                client.sendEndGameData(endGameData);
            } catch (IOException e) {
                System.out.println("Error sending endGameData to client " + client + ": " + e.getMessage());
            }
        }
    }

    private void logGameState() {
        for (Stone[] stones : getGoGame().getBoard()) {
            for (Stone stone : stones) {
                if (stone != null)
                    System.out.println(stone);
            }
        }
    }

    public GoGame getGoGame() {
        return goGame;
    }

    public boolean isLastMoveResignation() {
        return lastMoveResignation;
    }

    public void setLastMoveResignation(boolean lastMoveResignation) {
        this.lastMoveResignation = lastMoveResignation;
    }

    public static void main(String[] args) {
        GoGameServer server = new GoGameServer();
        server.startServer();
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void broadcastGameData(GameData gameData) {
        for (ClientHandler client : clients) {
            try {
//                System.out.println("Sending game data to client: " + client);
                System.out.println(gameData);
                client.sendGameData(gameData);
            } catch (IOException e) {
//                System.out.println("Error sending data to client " + client + ": " + e.getMessage());
            }
        }
    }
}
