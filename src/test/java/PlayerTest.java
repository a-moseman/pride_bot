import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

public class PlayerTest {

    @Test
    public void addPride() {
        Player player = new Player("", "");
        player.addPride(40);
        assertEquals(2, player.getEgo());
    }

    @Test
    public void removePride() {
        Player player = new Player("", "");
        player.addPride(40);
        player.removePride(20);
        assertEquals(1, player.getEgo());
        assertEquals(10, player.getPride());
    }

    @Test
    public void killPlayer() {
        Player player = new Player("", "");
        player.addPride(10);
        player.removePride(9999);
        assertEquals(1, player.getEgo());
        assertEquals(1, player.getDeaths());
        assertEquals(10, player.getPride());
    }

    @Test
    public void multipleLevelUp() {
        Player player = new Player("", "");
        player.addPride(120);
        assertEquals(3, player.getEgo());
        assertEquals(0, player.getPride());
    }
}
