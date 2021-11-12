package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.jetty.util.DateCache.Tick;
import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.exceptions.InvalidActionException;
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
    public void TestBomb() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        int spider = -1;
        while (spider != 0){
            int spidercount = 0;
            dm.newGame("bombmap", "Peaceful");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if(e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
        }
        dm.tick(null, Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> {
            dm.tick("7", Direction.NONE);
        });
        dm.tick(null, Direction.RIGHT);
        dm.tick("7", Direction.NONE);
        int DestroyedWalls = 0;
        int preservedWalls = 0;
        for (Entity entity: game.getEntities()){
            if (entity.getId().equals("3")) {
                DestroyedWalls++;
            }
            if (entity.getId().equals("4")) {
                DestroyedWalls++;
            }
            if (entity.getId().equals("5")) {
                preservedWalls++;
            }
        }
        assertTrue(DestroyedWalls == 0);
        assertTrue(preservedWalls == 1);

    }

    @Test
    public void TestTick() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("basicmap1", "Peaceful");

        DungeonMania game = dm.getLoadedGame();
        Character player = game.getCharacter();

        assertTrue(player.getPos().equals(new Position(0, 0)));

        // should walk into a wall
        dm.tick(null, Direction.RIGHT);
        assertTrue(player.getPos().equals(new Position(0, 0)));

        // should move properly here
        dm.tick(null, Direction.DOWN);
        assertTrue(player.getPos().equals(new Position(0, 1)));
    }

    @Test
    public void testItem() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;

        int spider = -1;
        while (spider != 0) {
            int spidercount = 0;
            dm.newGame("basicmap13", "Standard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if (e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
        }

        Character player = game.getCharacter();

        // player on (0, 0)
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);
        // player picks up bomb, should have it in their inventory
        assertTrue(game.getItems().size() == 1);

        for (int i = 0; i < 4; i++) {
            dm.tick(null, Direction.RIGHT);
        }

        // should have picked up three potions, decrements as we use them
        assertTrue(game.getItems().size() == 4);
        // health maxes out
        dm.tick("6", Direction.NONE);
        assertTrue(player.getHealth() == 30);
        assertTrue(game.getItems().size() == 3);
        dm.tick("7", Direction.NONE);
        assertTrue(game.getItems().size() == 2);
        dm.tick("8", Direction.NONE);
        assertTrue(game.getItems().size() == 1);
    }

    @Test
    public void testZombieSpawn() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        int spider = -1;
        while (spider != 0) {
            int spidercount = 0;
            dm.newGame("basicmap6", "Standard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if (e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;

        }

        assertThrows(InvalidActionException.class, () -> {
            dm.interact("2");
        });
        // no sword error
        assertTrue(game.getEntities().size() == 21);
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);
        assertTrue(game.getEntities().size() == 20);
        // pickup sword
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);
        dm.interact("2");
        assert (game.getEntities().size() == 19);
    }

    @Test
    public void TestMercenaryInteractAndThrowingErrors() {
        DungeonManiaController dm = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> {
            dm.newGame("failure", "Peaceful");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            dm.newGame("advanced", "pain");
        });
        dm.newGame("advanced", "Peaceful");
        assertThrows(IllegalArgumentException.class, () -> {
            dm.interact("HELLO");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            dm.build("sadness");
        });
        DungeonMania game = dm.getLoadedGame();
        dm.tick(null, Direction.LEFT);
        dm.tick(null, Direction.LEFT);
        dm.tick(null, Direction.LEFT);
        dm.tick(null, Direction.LEFT);
        dm.tick(null, Direction.LEFT);
        assertThrows(InvalidActionException.class, () -> {
            dm.interact("43");
        });
    }

    @Test
    public void TestSaveLoad() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("basicmap2", "Peaceful");

        DungeonMania game = dm.getLoadedGame();
        Character player = game.getCharacter();

        // walk down thrice, pick up treasure
        dm.tick(null, Direction.DOWN);
        dm.tick(null, Direction.DOWN);
        dm.tick(null, Direction.DOWN);

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
        DungeonMania game = null;

        int spider = -1;
        while (spider != 0) {
            int spidercount = 0;
            dm.newGame("basicmap7", "Peaceful");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if (e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
        }

        for (int i = 0; i < 7; i++) {
            dm.tick(null, Direction.RIGHT);
        }

        dm.build("bow");
        dm.build("shield");

        assertTrue(dm.getLoadedGame().getItems().size() == 2);
    }
}