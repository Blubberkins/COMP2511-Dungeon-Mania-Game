package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PrimsTest {
    @Test
    public void testGenerate() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.generateDungeon(1, 1, 49, 49, "Peaceful");

        assertTrue(dm.getLoadedGame() != null);
    }
}
