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
        assertEquals(1, player.getShame());
        assertEquals(10, player.getPride());
    }

    @Test
    public void multipleLevelUp() {
        Player player = new Player("", "");
        player.addPride(110);
        assertEquals(3, player.getEgo());
        assertEquals(0, player.getPride());
    }

    @Test
    public void testAscend() {
        Player player = new Player("", "").loadData(0, 11, 0, 0);
        assertEquals(0, player.getAscendancy());
        player.ascend();
        assertEquals(1, player.getAscendancy());
        assertEquals(1, player.getEgo());
    }

    @Test
    public void testAscendancyShameEffect() {
        Player player = new Player("", "").loadData(10, 1, 2, 3);
        player.addPride(1);
        assertEquals(12, player.getPride());
    }
}
