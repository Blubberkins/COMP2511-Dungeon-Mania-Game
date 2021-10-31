package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.response.models.*;

import java.util.List;

// By Liam

public class TestSpider {
    @Test
    public void reverse() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("basicmap11", "Standard");

        DungeonMania game = dm.getLoadedGame();
        Character player = game.getCharacter();
        // maxing out the hp of the character so they don't die to random spiders
        player.setHealth(Integer.MAX_VALUE);

        for (int i = 0; i < 5; i++) {
            dm.tick("", Direction.UP);
            dm.tick("", Direction.DOWN);
            dm.tick("", Direction.RIGHT);
        }

        // game shouldn't be completed because the player gets stuck behind two boulders
        assertFalse(dm.getLoadedGame() == null);
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