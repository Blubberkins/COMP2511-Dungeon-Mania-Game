package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.response.models.*;

import java.util.List;

// By Liam

public class TestMotion {
    @Test
    public void reverse() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("basicmap11", "Standard");
        DungeonMania game = dm.getLoadedGame();
        for (int i = 0; i < 5; i++) {
            dm.tick(null, Direction.UP);
            dm.tick(null, Direction.DOWN);
            dm.tick(null, Direction.RIGHT);
        }

        // game shouldn't be completed because the player gets stuck behind two boulders
        assertFalse(dm.getLoadedGame() == null);
    }

    @Test
    public void testDoorPortal() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        int spider = -1;
        while (spider != 0){
            int spidercount = 0;
            dm.newGame("basicmap12", "standard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if(e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;

        }
        Character player = game.getCharacter();
        assertTrue(player.getPos().getX() == 1);
        assertTrue(player.getPos().getY() == 1);
        // pick up key
        dm.tick(null, Direction.RIGHT);
        assertTrue(game.getItems().size() == 1);
;

        // walk through door then portal
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);

        assertTrue(player.getPos().getX() == 6);
        assertTrue(player.getPos().getY() == 1);

    }
    
    public Boolean hasSpider(DungeonMania game, Position position) {
        List<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity.getType().compareTo("spider") == 0) {
                return true;
            }
        }

        return false;
    }
}