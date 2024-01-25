package com.goboardgame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private GoGame goGame;
    private GoGameServer goGameServer;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public ClientHandler(Socket clientSocket, GoGame goGame, GoGameServer goGameServer) {
        this.clientSocket = clientSocket;
        this.goGame = goGame;
        this.goGameServer = goGameServer;

        try {
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Odbieranie danych od klienta
                Object receivedData = inputStream.readObject();

                // Implementuj odpowiednie operacje na goGame w zależności od odebranych danych
                if (receivedData instanceof MoveData) {
                    MoveData moveData = (MoveData) receivedData;
                    handleMove(moveData);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendGameData(GameData gameData) throws IOException {
        outputStream.writeObject(gameData);
    }

    private void handleMove(MoveData moveData) {
        int x = moveData.getX();
        int y = moveData.getY();

        if (goGame.placeStone(x, y)) {
            GameData updatedGameData = new GameData(goGame);
            goGameServer.broadcastGameData(updatedGameData);
        }
    }
}
