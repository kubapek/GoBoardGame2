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

    public ClientHandler(Socket clientSocket, GoGameServer goGameServer) {
        this.clientSocket = clientSocket;
        this.goGameServer = goGameServer;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Receiving data from the client
                Object receivedData = inputStream.readObject();
                System.out.println(receivedData);
                // Implement the appropriate operations on the server's game logic
                if (receivedData instanceof MoveData) {
                    MoveData moveData = (MoveData) receivedData;
                    goGameServer.handleMove(moveData);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendGameData(GameData gameData) throws IOException {
        outputStream.writeObject(gameData);
    }
}
