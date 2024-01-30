package com.goboardgame;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.goboardgame.dto.EndGameData;
import com.goboardgame.dto.GameData;
import com.goboardgame.dto.MoveData;
import com.goboardgame.dto.WinnerInfo;
import com.goboardgame.server.ClientHandler;
import com.goboardgame.server.GoGameServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GoGameServerTest {

    @Test
    public void testGetGoGame() {
        GoGameServer server = new GoGameServer();
        assertNotNull(server.getGoGame());
    }

    @Test
    void testRemoveClient() {
        GoGameServer server = new GoGameServer();
        ClientHandler client1 = mock(ClientHandler.class);
        ClientHandler client2 = mock(ClientHandler.class);
        server.clients.add(client1);
        server.clients.add(client2);

        server.removeClient(client1);

        assert !server.clients.contains(client1);
        assert server.clients.contains(client2);
    }

    @Test
    void testIsLastMoveResignation() {
        GoGameServer server = new GoGameServer();

        boolean result = server.isLastMoveResignation();

        assertFalse(result);
    }

    @Test
    void testSetLastMoveResignation() {
        GoGameServer server = new GoGameServer();

        server.setLastMoveResignation(true);

        assertTrue(server.isLastMoveResignation());
    }

    @Test
    void testHandleMove() {
        GoGameServer server = spy(new GoGameServer());
        GoGame mockedGoGame = mock(GoGame.class);
        ClientHandler sender = mock(ClientHandler.class);
        server.goGame = mockedGoGame;
        Stone[][] board = new Stone[9][9];

        when(sender.getPlayerColor()).thenReturn(Stone.StoneColor.BLACK);
        when(mockedGoGame.getCurrentPlayer()).thenReturn(Stone.StoneColor.BLACK);
        when(mockedGoGame.placeStone(1, 1, Stone.StoneColor.BLACK)).thenReturn(true);
        when(mockedGoGame.getBoard()).thenReturn(board);

        server.handleMove(new MoveData(1, 1), sender);

        verify(sender, times(1)).getPlayerColor();
        verify(mockedGoGame, times(1)).getCurrentPlayer();
        verify(mockedGoGame, times(1)).placeStone(1, 1, Stone.StoneColor.BLACK);
        verify(server, times(1)).broadcastGameData(any(GameData.class));
        verify(server, times(1)).setLastMoveResignation(false);
    }

    @Test
    void testBroadcastSurrenderData() throws IOException {
        GoGameServer server = spy(new GoGameServer());
        ClientHandler sender = mock(ClientHandler.class);
        ClientHandler client1 = mock(ClientHandler.class);
        ClientHandler client2 = mock(ClientHandler.class);
        server.clients = Collections.synchronizedSet(new HashSet<>(Set.of(client1, client2)));

        when(sender.getPlayerColor()).thenReturn(Stone.StoneColor.BLACK);
        when(client1.getPlayerColor()).thenReturn(Stone.StoneColor.BLACK);
        when(client2.getPlayerColor()).thenReturn(Stone.StoneColor.WHITE);

        server.broadcastSurrenderData(sender);

        verify(sender, times(1)).getPlayerColor();
        verify(client1, times(1)).sendWinnerInfo(any(WinnerInfo.class));
        verify(client2, times(1)).sendWinnerInfo(any(WinnerInfo.class));
    }

    @Test
    void testBroadcastEndGameData() throws IOException {
        GoGameServer server = spy(new GoGameServer());
        ClientHandler client1 = mock(ClientHandler.class);
        ClientHandler client2 = mock(ClientHandler.class);
        server.clients = Collections.synchronizedSet(new HashSet<>(Set.of(client1, client2)));
        EndGameData endGameData = mock(EndGameData.class);

        server.broadcastEndGameData(endGameData);

        verify(client1, times(1)).sendEndGameData(endGameData);
        verify(client2, times(1)).sendEndGameData(endGameData);
    }
}
