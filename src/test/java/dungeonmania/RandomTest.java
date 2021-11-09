
package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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

        assertEquals(Arrays.asList(3, 0, 1, 0, 1, 3, 0, 3, 0, 2), numSpiders);
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
}