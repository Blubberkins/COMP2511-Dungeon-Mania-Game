package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Position;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class RandomTest {
    @Test
    public void testSeededSpiderSpawns() {
        DungeonManiaController dm = new DungeonManiaController();
        List<Integer> numSpiders = new ArrayList<Integer>();

        for (int i = 0; i < 10; i++) {
            dm.newGame("basicmap1", "Standard");
            DungeonMania game = dm.getLoadedGame();

            numSpiders.add(countSpiders(game));
        }

        assertEquals(Arrays.asList(2, 2, 4, 4, 1, 0, 4, 1, 2, 1), numSpiders);
    }

    @Test
    public void testSpiderSpawnPos() {
        DungeonManiaController dm = new DungeonManiaController();

        for (int i = 0; i < 10; i++) {
            dm.newGame("basicmap1", "Standard");
            DungeonMania game = dm.getLoadedGame();

            if (countSpiders(game) == 1) {
                // this is a stub, need to fill it with whatever position gets generated
                assertEquals(getSingleSpider(game).getPos(), new Position(0, 0));

            }
        }
    }

    public int countSpiders(DungeonMania game) {
        int count = 0;

        for (Entity entity : game.getEntities()) {
            if (entity instanceof Spider) {
                count += 1;
            }
        }

        return count;
    }

    public Entity getSingleSpider(DungeonMania game) {
        for (Entity entity : game.getEntities()) {
            if (entity instanceof Spider) {
                return entity;
            }
        }
        return null;
    }
}
