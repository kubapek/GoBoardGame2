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
        this.goGame = new GoGame(19); // Assume that GoGame is a class managing the game logic
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

    public void broadcastGameData(GameData gameData) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                try {
                    client.sendGameData(gameData);
                } catch (IOException e) {
                    System.out.println("Error sending data to a client: " + e.getMessage());
                    clients.remove(client);
                }
            }
        }
    }

    public void handleMove(MoveData moveData) {
        System.out.println("elo");
        // Logic for handling moves
        // Update goGame and send the updated state to all clients

        int x = moveData.getX();
        int y = moveData.getY();
        Stone.StoneColor currentPlayer = goGame.getCurrentPlayer();

        if (goGame.placeStone(x, y, currentPlayer)) {
            System.out.println("Gracz " + currentPlayer + " postawił kamień na pozycji x=" + x + ", y=" + y);
            goGame.togglePlayer();
            GameData updatedGameData = new GameData(goGame);
            broadcastGameData(updatedGameData);
        }
    }



    public GoGame getGoGame() {
        return goGame;
    }

    public static void main(String[] args) {
        GoGameServer server = new GoGameServer();
        server.startServer();
    }


}
