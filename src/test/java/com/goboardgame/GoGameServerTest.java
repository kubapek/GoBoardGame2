package com.goboardgame;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class GoGameServerTest {

    @Test
    public void testGetGoGame() {
        GoGameServer server = new GoGameServer();
        assertNotNull(server.getGoGame());
    }

}
