package com.goboardgame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoGameTest {

    @Test
    void testRemoveCapturedStone() {
        GoGame game = new GoGame(9);

        // Ustawienie kamieni na planszy w taki sposób, aby kamień biały (WHITE) został otoczony przez czarne (BLACK)
        game.placeStone(0, 1, Stone.StoneColor.BLACK); // BLACK
        game.placeStone(0, 0, Stone.StoneColor.WHITE); // WHITE
        assertNotNull(game.getBoard()[0][0], "Kamień WHITE powinien być na planszy przed usunięciem.");
        game.placeStone(1, 0, Stone.StoneColor.BLACK); // BLACK
        assertNull(game.getBoard()[0][0], "Kamień WHITE powinien zostać usunięty.");
        game.placeStone(1, 1, Stone.StoneColor.WHITE); // WHITE
        game.placeStone(2, 1, Stone.StoneColor.BLACK); // BLACK
        game.placeStone(2, 2, Stone.StoneColor.WHITE); // WHITE
        assertNotNull(game.getBoard()[1][1], "Kamień WHITE powinien być na planszy przed usunięciem.");
        game.placeStone(1, 2, Stone.StoneColor.BLACK); // BLACK - ruch mający usunąć kamień WHITE

        assertNull(game.getBoard()[1][1], "Kamień WHITE powinien zostać usunięty.");

    }

    @Test
    void testInitialBoardSize() {
        GoGame game = new GoGame(19);
        assertEquals(19, game.getBoardSize());
    }

    @Test
    void testPlaceStoneValidMove() {
        GoGame game = new GoGame(9);
        assertTrue(game.placeStone(4, 4, Stone.StoneColor.BLACK));
        assertEquals(Stone.StoneColor.WHITE, game.getCurrentPlayer());
    }

    @Test
    void testPlaceStoneInvalidMove() {
        GoGame game = new GoGame(9);
        assertTrue(game.placeStone(4, 4, Stone.StoneColor.BLACK));
        assertFalse(game.placeStone(4, 4, Stone.StoneColor.WHITE));
    }

    @Test
    void testKoRule() {
        GoGame game = new GoGame(9);

        // Set up a situation where Ko rule applies
        assertTrue(game.placeStone(0, 1, Stone.StoneColor.BLACK));
        assertTrue(game.placeStone(1, 1, Stone.StoneColor.WHITE));
        assertTrue(game.placeStone(1, 0, Stone.StoneColor.BLACK));
        assertTrue(game.placeStone(2, 1, Stone.StoneColor.BLACK));
        assertTrue(game.placeStone(1, 2, Stone.StoneColor.BLACK));

        assertFalse(game.placeStone(1, 1, Stone.StoneColor.WHITE));
        assertFalse(game.placeStone(1, 1, Stone.StoneColor.BLACK));
    }

    @Test
    void testCapturingStones() {
        GoGame game = new GoGame(9);

        // Set up a situation where stones are captured
        assertTrue(game.placeStone(0, 1, Stone.StoneColor.BLACK));
        assertTrue(game.placeStone(1, 1, Stone.StoneColor.WHITE));
        assertTrue(game.placeStone(1, 0, Stone.StoneColor.BLACK));
        assertTrue(game.placeStone(2, 1, Stone.StoneColor.BLACK));
        assertTrue(game.placeStone(1, 2, Stone.StoneColor.BLACK));

        assertEquals(1, game.getPlayer1Score());
        assertEquals(0, game.getPlayer2Score());
    }

}
