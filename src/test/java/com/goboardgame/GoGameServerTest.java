package com.goboardgame;
import static org.junit.jupiter.api.Assertions.*;

import com.goboardgame.server.GoGameServer;
import org.junit.jupiter.api.Test;

public class GoGameServerTest {

    @Test
    public void testGetGoGame() {
        GoGameServer server = new GoGameServer();
        assertNotNull(server.getGoGame());
    }

}
