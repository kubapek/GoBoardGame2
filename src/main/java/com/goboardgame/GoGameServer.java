package com.goboardgame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GoGameServer {
    private static final int PORT = 8000;
    private final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());
    private GoGame goGame;

    public GoGameServer() {
        this.goGame = new GoGame(19);
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("GoGame server is running on port " + PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket);

                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    clients.add(clientHandler);

                    new Thread(clientHandler).start();
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

        if (goGame.placeStone(x, y, currentPlayer)) {
            System.out.println("Player " + currentPlayer + " placed a stone at position x=" + x + ", y=" + y);
            GameData updatedGameData = new GameData(goGame);
            broadcastGameData(updatedGameData, sender);
        }
    }



    public GoGame getGoGame() {
        return goGame;
    }

    public static void main(String[] args) {
        GoGameServer server = new GoGameServer();
        server.startServer();
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    private void broadcastGameData(GameData gameData, ClientHandler excludeClient) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != excludeClient) {
                    try {
                        client.sendGameData(gameData);
                    } catch (IOException e) {
                        System.out.println("Error sending data to a client: " + e.getMessage());
                        clients.remove(client);
                    }
                }
            }
        }
    }

}
