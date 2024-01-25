package com.goboardgame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GoGameServer {
    private GoGame goGame;
    private List<ClientHandler> clients;

    public GoGameServer(int boardSize) {
        this.goGame = new GoGame(boardSize);
        this.clients = new ArrayList<>();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            System.out.println("Serwer jest gotowy do przyjmowania połączeń...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket, goGame, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastGameData(GameData gameData) {
        for (ClientHandler client : clients) {
            try {
                client.sendGameData(gameData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        GoGameServer server = new GoGameServer(19);
        server.startServer();
    }
}
