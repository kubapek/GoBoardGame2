package com.goboardgame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GoGameTest {
    private GoGame goGame;

    @BeforeEach
    void setUp() {
        goGame = new GoGame(9);
    }

    @Test
    void testRemoveCapturedStone() {

        // Ustawienie kamieni na planszy w taki sposób, aby kamień biały (WHITE) został otoczony przez czarne (BLACK)
        goGame.placeStone(0, 1, Stone.StoneColor.BLACK); // BLACK
        goGame.placeStone(0, 0, Stone.StoneColor.WHITE); // WHITE
        assertNotNull(goGame.getBoard()[0][0], "Kamień WHITE powinien być na planszy przed usunięciem.");
        goGame.placeStone(1, 0, Stone.StoneColor.BLACK); // BLACK
        assertNull(goGame.getBoard()[0][0], "Kamień WHITE powinien zostać usunięty.");
        goGame.placeStone(1, 1, Stone.StoneColor.WHITE); // WHITE
        goGame.placeStone(2, 1, Stone.StoneColor.BLACK); // BLACK
        goGame.placeStone(2, 2, Stone.StoneColor.WHITE); // WHITE
        assertNotNull(goGame.getBoard()[1][1], "Kamień WHITE powinien być na planszy przed usunięciem.");
        goGame.placeStone(1, 2, Stone.StoneColor.BLACK); // BLACK - ruch mający usunąć kamień WHITE

        assertNull(goGame.getBoard()[1][1], "Kamień WHITE powinien zostać usunięty.");

    }

    @Test
    void testInitialBoardSize() {
        assertEquals(9, goGame.getBoardSize());
    }

    @Test
    void testPlaceStoneValidMove() {
        assertTrue(goGame.placeStone(4, 4, Stone.StoneColor.BLACK));
        assertEquals(Stone.StoneColor.WHITE, goGame.getCurrentPlayer());
    }

    @Test
    void testPlaceStoneInvalidMove() {
        assertTrue(goGame.placeStone(4, 4, Stone.StoneColor.BLACK));
        assertFalse(goGame.placeStone(4, 4, Stone.StoneColor.WHITE));
    }

    @Test
    void isKo_validKo_returnsTrue() {
        goGame.placeStone(0, 1, Stone.StoneColor.BLACK);
        goGame.placeStone(1, 0, Stone.StoneColor.BLACK);
        goGame.placeStone(1, 2, Stone.StoneColor.BLACK);
        goGame.placeStone(1, 1, Stone.StoneColor.WHITE);
        goGame.placeStone(2, 0, Stone.StoneColor.WHITE);
        goGame.placeStone(3, 1, Stone.StoneColor.WHITE);
        goGame.placeStone(2, 2, Stone.StoneColor.WHITE);

        goGame.placeStone(2, 1, Stone.StoneColor.BLACK);

        assertTrue(goGame.isKo(1, 1, Stone.StoneColor.WHITE));
    }

    @Test
    void findGroup_findsConnectedStones() {
        goGame.placeStone(3, 3, Stone.StoneColor.BLACK);
        goGame.placeStone(4, 3, Stone.StoneColor.BLACK);
        goGame.placeStone(4, 4, Stone.StoneColor.BLACK);

        Set<Point> group = goGame.findGroup(3, 3);
        assertEquals(3, group.size());
    }

    @Test
    void hasLiberties_groupHasLiberties_returnsTrue() {
        goGame.placeStone(3, 3, Stone.StoneColor.BLACK);
        goGame.placeStone(4, 3, Stone.StoneColor.BLACK);

        Set<Point> group = goGame.findGroup( 3, 3);
        assertTrue(goGame.hasLiberties(goGame.getBoard(), group));
    }

    @Test
    void areBoardsEqual_equalBoards_returnsTrue() {
        Stone[][] board1 = goGame.getBoard();
        Stone[][] board2 = goGame.deepCopyBoard(board1);

        assertTrue(goGame.areBoardsEqual(board1, board2));
    }

    @Test
    void areBoardsEqual_equalBoards_returnsFalse() {
        Stone[][] board1 = goGame.getBoard();
        Stone[][] board2 = new Stone[9][9];
        board2[0][1] = new Stone(Stone.StoneColor.WHITE, 0, 0);

        assertFalse(goGame.areBoardsEqual(board1, board2));
    }

    @Test
    void deepCopyBoard_copiesBoardCorrectly() {
        goGame.placeStone(3, 3, Stone.StoneColor.BLACK);

        Stone[][] originalBoard = goGame.getBoard();
        Stone[][] copiedBoard = goGame.deepCopyBoard(originalBoard);

        originalBoard[3][3] = null;

        assertNotNull(copiedBoard[3][3]);
    }

    @Test
    void setGameEnded_gameEnds() {
        goGame.setGameEnded();
        assertTrue(goGame.isGameEnded());
    }

    @Test
    void togglePlayer_switchesPlayers() {
        Stone.StoneColor initialPlayer = goGame.getCurrentPlayer();
        goGame.togglePlayer();
        assertNotEquals(initialPlayer, goGame.getCurrentPlayer());
    }

    @Test
    void incrementPlayersCount_increaseCount_countIncremented() {
        int initialCount = goGame.getPlayersCount();
        goGame.incrementPlayersCount();
        assertEquals(initialCount + 1, goGame.getPlayersCount());
    }

    @Test
    void getPlayer1Score_initialScore_returnsZero() {
        assertEquals(0, goGame.getPlayer1Score());
    }

    @Test
    void getPlayer2Score_initialScore_returnsZero() {
        assertEquals(0, goGame.getPlayer2Score());
    }
}
