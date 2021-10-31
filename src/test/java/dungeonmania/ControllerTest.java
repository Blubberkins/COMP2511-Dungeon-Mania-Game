package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.response.models.*;

import java.util.List;

// By Liam

public class ControllerTest {
    @Test
    public void TestNewGame() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("basicmap1", "Peaceful");

        // checking the game exists and is currently laoded
        assertTrue(dm.getGames().size() == 1);
        DungeonMania game = dm.getLoadedGame();
        assertFalse(game == null);

        // entities exist in the game
        assertTrue(game.getEntities().size() > 0);
    }

    @Test
    public void TestTick() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("basicmap1", "Peaceful");

        DungeonMania game = dm.getLoadedGame();
        Character player = game.getCharacter();

        assertTrue(player.getPos().equals(new Position(0, 0)));

        // should walk into a wall
        dm.tick("", Direction.RIGHT);
        assertTrue(player.getPos().equals(new Position(0, 0)));

        // should move properly here
        dm.tick("", Direction.DOWN);
        assertTrue(player.getPos().equals(new Position(0, 1)));
    }

    @Test
    public void TestSaveLoad() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("basicmap2", "Peaceful");

        DungeonMania game = dm.getLoadedGame();
        Character player = game.getCharacter();

        // walk down thrice, pick up treasure
        dm.tick("", Direction.DOWN);
        dm.tick("", Direction.DOWN);
        dm.tick("", Direction.DOWN);
        assertTrue(player.getPos().equals(new Position(0, 3)));
        assertTrue(game.getItems().size() == 1);

        // no game should be loaded
        dm.saveGame("save1");
        assertTrue(dm.getLoadedGame() == null);

        // loading the game now, everything should be in the same spot as before
        dm.loadGame("Peaceful-basicmap2-0-.json");
        assertFalse(dm.getLoadedGame() == null);

        // should restore the inventory and the player's position and all entities on
        // the map
        game = dm.getLoadedGame();
        player = game.getCharacter();
        assertTrue(player.getPos().equals(new Position(0, 3)));
        assertTrue(game.getItems().size() == 1);

        assertTrue(game.getEntities().size() == 7);
    }

    @Test
    public void testBuild() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("basicmap7", "Peaceful");

        for (int i = 0; i < 7; i++) {
            dm.tick(null, Direction.RIGHT);
        }

        dm.build("bow");
        dm.build("shield");

        assertTrue(dm.getLoadedGame().getItems().size() == 2);
    }
}