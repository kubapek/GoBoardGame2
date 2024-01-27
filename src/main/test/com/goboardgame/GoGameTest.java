package com.goboardgame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GoGameTest {

    @Test
    void testRemoveCapturedStone() {
        GoGame game = new GoGame(9);

        // Ustawienie kamieni na planszy w taki sposób, aby kamień biały (WHITE) został otoczony przez czarne (BLACK)
        game.placeStone(0, 1, Stone.StoneColor.BLACK); // BLACK
        game.placeStone(0, 0, Stone.StoneColor.WHITE); // WHITE
        game.placeStone(1, 0, Stone.StoneColor.BLACK); // BLACK
        game.placeStone(1, 1, Stone.StoneColor.WHITE); // WHITE
        game.placeStone(0, 2, Stone.StoneColor.BLACK); // BLACK
        game.placeStone(2, 2, Stone.StoneColor.WHITE); // WHITE
        assertNotNull(game.getBoard()[1][1], "Kamień WHITE powinien być na planszy przed usunięciem.");
        game.placeStone(1, 2, Stone.StoneColor.BLACK); // BLACK - ruch mający usunąć kamień WHITE

        assertNull(game.getBoard()[1][1], "Kamień WHITE powinien zostać usunięty.");
    }
}
