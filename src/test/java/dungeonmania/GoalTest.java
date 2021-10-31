package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.response.models.*;

import java.util.List;

// By Liam

public class GoalTest {
    @Test
    public void testExit() {
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        dungeonManiaController.newGame("basicmap1", "Peaceful");

        Character player = dungeonManiaController.getLoadedGame().getCharacter();

        Position position = new Position(0, 0);
        assertTrue(player.getPos().equals(position));

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick("", Direction.DOWN);
        }

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick("", Direction.RIGHT);
        }

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick("", Direction.UP);
        }

        position = new Position(3, 0);
        assertTrue(player.getPos().equals(position));

        // the player is supposed to reach the exit after this move
        dungeonManiaController.tick("", Direction.LEFT);

        // run a check for goal completion
        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void testTreasure() {
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        DungeonResponse game = dungeonManiaController.newGame("basicmap2", "Peaceful");

        List<EntityResponse> entities = game.getEntities();

        // track the player
        EntityResponse player = null;
        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("player") == 0) {
                player = entity;
                break;
            }
        }
        assertTrue(player != null);

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick("", Direction.DOWN);
        }

        // should have picked up the treasure on (0, 3)
        ItemResponse item1 = game.getInventory().get(0);
        assertTrue(item1.getType().compareTo("treasure") == 0);
        // but shouldn't have finished the level, there is still treasure at (2, 0)
        assertFalse(dungeonManiaController.getLoadedGame() == null);

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick("", Direction.RIGHT);
        }

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick("", Direction.UP);
        }

        // the player is supposed to reach the final treasure after this move
        dungeonManiaController.tick("", Direction.LEFT);
        // should have two pieces of treasure (inventory size 2)
        assertTrue(game.getInventory().size() == 2);

        // run a check for goal completion, should be okay this time
        assertTrue(dungeonManiaController.getLoadedGame() == null);

    }

    @Test
    public void testBoulderMap() {
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        DungeonResponse game = dungeonManiaController.newGame("basicmap3", "Peaceful");

        List<EntityResponse> entities = game.getEntities();

        // track the player
        EntityResponse player = null;
        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("player") == 0) {
                player = entity;
                break;
            }
        }
        assertTrue(player != null);

        // player on (0, 0)
        dungeonManiaController.tick("", Direction.RIGHT);
        // now next to the boulder
        assertFalse(dungeonManiaController.getLoadedGame() == null);
        dungeonManiaController.tick("", Direction.RIGHT);
        // the player should now be on (2, 0) having pushed the boulder on to the switch
        // at (3, 0)
        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void testEnemy() {
        // TODO figure this out once random entity spawning is decided
        // currently assumes:
        // zombies have 1hp
        // player has more hp than the zombie does in one hit
        // sword has a base damage of 5, and at least two durability
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        DungeonResponse game = dungeonManiaController.newGame("basicmap6", "Standard");

        List<EntityResponse> entities = game.getEntities();

        // track the player, and the spawner
        EntityResponse player = null;
        EntityResponse spawner = null;
        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("player") == 0) {
                player = entity;
            } else if (entity.getType().compareTo("zombie_toast_spawner") == 0) {
                spawner = entity;
            }
        }
        assertTrue(player != null);
        assertTrue(spawner != null);

        // player starts on (0, 0), spawner on (5, 0)
        dungeonManiaController.tick("", Direction.RIGHT);

        // should have picked up the sword on (1, 0)
        ItemResponse sword = game.getInventory().get(0);
        assertTrue(game.getInventory().size() == 1);
        assertTrue(sword.getType().compareTo("sword") == 0);

        // going back and forth until the spawner spawns a zombie after 20 ticks
        for (int i = 0; i < 9; i++) {
            dungeonManiaController.tick("", Direction.RIGHT);
            dungeonManiaController.tick("", Direction.LEFT);
        }

        // player is currently on (1, 0), 19 ticks have passed
        dungeonManiaController.tick("", Direction.RIGHT);

        // player should now be on (2, 0)
        Position position = new Position(2, 0);
        assertTrue(player.getPosition().equals(position));

        // a zombie should have spawned on (4, 0), the only square a zombie can spawn
        EntityResponse zombie = null;
        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("zombie_toast") == 0) {
                zombie = entity;
                break;
            }
        }

        position = new Position(4, 0);
        assertTrue(zombie != null);
        assertTrue(zombie.getPosition().equals(position));

        // the zombie either moves to (3, 0), where it gets killed by the player in
        // combat
        dungeonManiaController.tick("sword", Direction.RIGHT);
        // if it has moved on top of the spawner on (5, 0), then it is forced to move
        // back to (4, 0)
        // on its second move, where it encounters the player and gets killed regardless
        dungeonManiaController.tick("sword", Direction.RIGHT);

        // should just now be the player and the spawner
        assertTrue(game.getEntities().size() == 2);
        // player should still have the sword
        assertTrue(game.getInventory().size() == 1);
        // game should not be complete because the spawner still exists
        assertFalse(dungeonManiaController.getLoadedGame() == null);

        // player now destroys the spawner, and beats the level
        dungeonManiaController.tick("sword", Direction.RIGHT);
        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void testGoalComposition1() {
        // GOAL: exit AND treasure
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        DungeonResponse game = dungeonManiaController.newGame("basicmap4", "Peaceful");

        List<EntityResponse> entities = game.getEntities();

        // track the player
        EntityResponse player = null;
        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("player") == 0) {
                player = entity;
                break;
            }
        }
        assertTrue(player != null);

        // player on (0, 0)
        dungeonManiaController.tick("", Direction.RIGHT);
        // now next to the treasure on (2, 0), player on (1, 0)
        assertFalse(dungeonManiaController.getLoadedGame() == null);
        dungeonManiaController.tick("", Direction.RIGHT);
        // the player should now be on (2, 0) having picked up the treasure
        assertTrue(game.getInventory().size() == 1);
        assertFalse(dungeonManiaController.getLoadedGame() == null);
        // the player should now be moving to the exit
        dungeonManiaController.tick("", Direction.RIGHT);
        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void testGoalComposition2() {
        // GOAL: exit OR (treasure AND boulders)
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        DungeonResponse game = dungeonManiaController.newGame("basicmap5", "Peaceful");

        List<EntityResponse> entities = game.getEntities();

        // track the player
        EntityResponse player = null;
        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("player") == 0) {
                player = entity;
                break;
            }
        }
        assertTrue(player != null);

        // player to head straight to the exit
        dungeonManiaController.tick("", Direction.RIGHT);
        dungeonManiaController.tick("", Direction.RIGHT);
        dungeonManiaController.tick("", Direction.RIGHT);

        assertTrue(dungeonManiaController.getLoadedGame() == null);

        // set up an identical new game
        DungeonManiaController dungeonManiaController2 = new DungeonManiaController();
        DungeonResponse game2 = dungeonManiaController2.newGame("basicmap5", "Peaceful");

        List<EntityResponse> entities2 = game2.getEntities();

        // track the player
        EntityResponse player2 = null;
        for (EntityResponse entity : entities2) {
            if (entity.getType().compareTo("player") == 0) {
                player2 = entity;
                break;
            }
        }
        assertTrue(player2 != null);

        // player to push the boulder (1, 1) onto the switch (2, 1)
        dungeonManiaController2.tick("", Direction.DOWN);
        dungeonManiaController2.tick("", Direction.RIGHT);
        // go up and take the treasure
        dungeonManiaController2.tick("", Direction.UP);
        dungeonManiaController2.tick("", Direction.RIGHT);

        assertTrue(dungeonManiaController.getLoadedGame() == null);

    }

    @Test
    public void testExitLast() {
        // same as testComposition1 but testing now for exit completion last
        // GOAL: exit AND treasure
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        DungeonResponse game = dungeonManiaController.newGame("basicmap4", "Peaceful");

        List<EntityResponse> entities = game.getEntities();

        // track the player
        EntityResponse player = null;
        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("player") == 0) {
                player = entity;
                break;
            }
        }
        assertTrue(player != null);

        // player on (0, 0), will dodge the treasure on (2, 0)
        // and go to the exit on (3, 0)
        dungeonManiaController.tick("", Direction.DOWN);
        dungeonManiaController.tick("", Direction.RIGHT);
        dungeonManiaController.tick("", Direction.RIGHT);
        dungeonManiaController.tick("", Direction.RIGHT);
        dungeonManiaController.tick("", Direction.UP);

        assertFalse(dungeonManiaController.getLoadedGame() == null);

        // but if we track back for the treasure we should complete the game
        dungeonManiaController.tick("", Direction.LEFT);
        dungeonManiaController.tick("", Direction.RIGHT);

        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }
}
