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
        Character player = game.getCharacter();
        // maxing out the hp of the character so they don't die to random spiders
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        for (int i = 0; i < 5; i++) {
            dm.tick(null, Direction.UP);
            dm.tick(null, Direction.DOWN);
            dm.tick(null, Direction.RIGHT);
            player.setHealth(Integer.MAX_VALUE);
            game.setCharacter(player);
        }

        // game shouldn't be completed because the player gets stuck behind two boulders
        assertFalse(dm.getLoadedGame() == null);
    }

    @Test
    public void testDoorPortal() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("basicmap12", "Standard");

        DungeonMania game = dm.getLoadedGame();
        Character player = game.getCharacter();
        // maxing out the hp of the character so they don't die to random spiders
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        assertTrue(player.getPos() == new Position(1, 1));
        // pick up key
        dm.tick(null, Direction.RIGHT);
        assertTrue(game.getItems().size() == 1);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        // walk through door then portal
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        assertTrue(player.getPos() == new Position(6, 1));
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