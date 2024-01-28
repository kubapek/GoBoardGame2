package com.goboardgame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

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
                Object receivedData = inputStream.readObject();
                if (receivedData instanceof MoveData) {
                    MoveData moveData = (MoveData) receivedData;
                    goGameServer.handleMove(moveData, this);
                }
            }
        } catch (SocketException e) {
            System.out.println("Zakończono grę");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            goGameServer.removeClient(this);
        }
    }

    public void sendGameData(GameData gameData) throws IOException {
        outputStream.writeObject(gameData);
    }
}
