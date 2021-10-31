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
        
        dungeonManiaController.newGame("basicmap1", "Standard");

        DungeonMania game = dungeonManiaController.getLoadedGame();
        Character player = dungeonManiaController.getLoadedGame().getCharacter();
        // maxing out the hp of the character so they don't die to random spiders
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        Position position = new Position(0, 0);
        assertTrue(player.getPos().equals(position));

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick(null, Direction.DOWN);
            player.setHealth(Integer.MAX_VALUE);
            game.setCharacter(player);
        }

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick(null, Direction.RIGHT);
            player.setHealth(Integer.MAX_VALUE);
            game.setCharacter(player);
        }

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick(null, Direction.UP);
            player.setHealth(Integer.MAX_VALUE);
            game.setCharacter(player);
        }

        position = new Position(3, 0);
        assertTrue(player.getPos().equals(position));

        // the player is supposed to reach the exit after this move
        dungeonManiaController.tick(null, Direction.LEFT);

        // run a check for goal completion
        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void testTreasure() {
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        dungeonManiaController.newGame("basicmap2", "Standard");
        DungeonMania game = dungeonManiaController.getLoadedGame();
        Character player = dungeonManiaController.getLoadedGame().getCharacter();
        // maxing out the hp of the character so they don't die to random spiders
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick(null, Direction.DOWN);
        }

        List<ItemResponse> inventory = game.getItemResponses();

        // should have picked up the treasure on (0, 3)
        ItemResponse item1 = inventory.get(0);
        assertTrue(item1.getType().compareTo("treasure") == 0);
        // but shouldn't have finished the level, there is still treasure at (2, 0)
        assertFalse(dungeonManiaController.getLoadedGame() == null);

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick(null, Direction.RIGHT);
            player.setHealth(Integer.MAX_VALUE);
            game.setCharacter(player);
        }

        for (int i = 0; i < 3; i++) {
            dungeonManiaController.tick(null, Direction.UP);
            player.setHealth(Integer.MAX_VALUE);
            game.setCharacter(player);
        }

        // the player is supposed to reach the final treasure after this move
        dungeonManiaController.tick(null, Direction.LEFT);
        // should have two pieces of treasure (inventory size 2)
        inventory = game.getItemResponses();
        assertTrue(inventory.size() == 2);

        // run a check for goal completion, should be okay this time
        assertTrue(dungeonManiaController.getLoadedGame() == null);

    }

    @Test
    public void testBoulderMap() {
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        DungeonMania game = null;
        int spider = -1;
        while (spider != 0){
            int spidercount = 0;
            dungeonManiaController.newGame("basicmap3", "Standard");
            game = dungeonManiaController.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if(e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
    
        }
        // player on (0, 0)
        dungeonManiaController.tick(null, Direction.RIGHT);
        // now next to the boulder
        assertFalse(dungeonManiaController.getLoadedGame() == null);
        dungeonManiaController.tick(null, Direction.RIGHT);
        // the player should now be on (2, 0) having pushed the boulder on to the switch
        // at (3, 0)
        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void testEnemy() {
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        DungeonMania game = null;
        int spider = -1;
        while (spider != 0){
            int spidercount = 0;
            dungeonManiaController.newGame("basicmap6", "Standard");
            game = dungeonManiaController.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if(e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
    
        }
        Character player = game.getCharacter();
        List<ItemResponse> inventory = game.getItemResponses();
        assertTrue(inventory.size() == 0);
        // maxing out the hp of the character so they don't die to random spiders

        // player starts on (0, 0), spawner on (5, 0)
        dungeonManiaController.tick(null, Direction.RIGHT);
        inventory = game.getItemResponses();

        // should have picked up the sword on (2, 1)
        ItemResponse sword = inventory.get(0);
        assertTrue(inventory.size() == 1);
        assertTrue(sword.getType().compareTo("sword") == 0);

        // going back and forth until the spawner spawns a zombie after 20 ticks
        for (int i = 0; i < 9; i++) {
            dungeonManiaController.tick(null, Direction.RIGHT);
            dungeonManiaController.tick(null, Direction.LEFT);
        }

        // player is currently on (1, 0), 19 ticks have passed
        dungeonManiaController.tick(null, Direction.RIGHT);

        // player should now be on (3, 1)
        Position position = new Position(3, 1);
        System.out.println(player.getPos());
        assertTrue(player.getPos().equals(position));

        // a zombie should have spawned on (5, 1), the only square a zombie can spawn
        List<EntityResponse> entities = game.getEntityResponses();
        EntityResponse zombie = null;
        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("zombie_toast") == 0) {
                zombie = entity;
                break;
            }
        }

        position = new Position(5, 1);
        assertTrue(zombie != null);
        System.out.println(zombie.getPosition());
        assertTrue(zombie.getPosition().equals(position));

        // the zombie either moves to (3, 0), where it gets killed by the player in
        // combat
        dungeonManiaController.tick(null, Direction.RIGHT);
        // if it has moved on top of the spawner on (5, 0), then it is forced to move
        // back to (4, 0)
        // on its second move, where it encounters the player and gets killed regardless
        while(dungeonManiaController.getLoadedGame().getCharacter().getInBattle()){
        dungeonManiaController.tick(null, Direction.RIGHT);
        }
        // should just now be the player, the spawner, and walls
        
        assertTrue(game.getEntities().size() == 20);
        // player should still have the sword
        inventory = game.getItemResponses();
        assertTrue(inventory.size() == 1);
        // game should not be complete because the spawner still exists
        assertFalse(dungeonManiaController.getLoadedGame() == null);

        // player now destroys the spawner, and beats the level
        dungeonManiaController.interact("2");
        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void testGoalComposition1() {
        // GOAL: exit AND treasure
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        dungeonManiaController.newGame("basicmap4", "Standard");
        DungeonMania game = dungeonManiaController.getLoadedGame();
        Character player = game.getCharacter();
        // maxing out the hp of the character so they don't die to random spiders
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        // player on (0, 0)
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        // now next to the treasure on (2, 0), player on (1, 0)
        assertFalse(dungeonManiaController.getLoadedGame() == null);
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        // the player should now be on (2, 0) having picked up the treasure
        assertTrue(game.getItems().size() == 1);
        assertFalse(dungeonManiaController.getLoadedGame() == null);
        // the player should now be moving to the exit
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void testGoalComposition2() {
        // GOAL: exit OR (treasure AND boulders)
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        dungeonManiaController.newGame("basicmap5", "Standard");
        DungeonMania game = dungeonManiaController.getLoadedGame();
        Character player = dungeonManiaController.getLoadedGame().getCharacter();
        // maxing out the hp of the character so they don't die to random spiders
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        // player to head straight to the exit
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        assertTrue(dungeonManiaController.getLoadedGame() == null);

        // set up an identical new game
        dungeonManiaController.newGame("basicmap5", "Standard");
        // player to push the boulder (1, 1) onto the switch (2, 1)
        dungeonManiaController.tick(null, Direction.DOWN);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        // go up and take the treasure
        dungeonManiaController.tick(null, Direction.UP);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void testExitLast() {
        // same as testComposition1 but testing now for exit completion last
        // GOAL: exit AND treasure
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        dungeonManiaController.newGame("basicmap4", "Standard");
        DungeonMania game = dungeonManiaController.getLoadedGame();
        Character player = dungeonManiaController.getLoadedGame().getCharacter();
        // maxing out the hp of the character so they don't die to random spiders
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        // player on (0, 0), will dodge the treasure on (2, 0)
        // and go to the exit on (3, 0)
        dungeonManiaController.tick(null, Direction.DOWN);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        dungeonManiaController.tick(null, Direction.UP);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        assertFalse(dungeonManiaController.getLoadedGame() == null);

        // but if we track back for the treasure we should complete the game
        dungeonManiaController.tick(null, Direction.LEFT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);
        dungeonManiaController.tick(null, Direction.RIGHT);
        player.setHealth(Integer.MAX_VALUE);
        game.setCharacter(player);

        assertTrue(dungeonManiaController.getLoadedGame() == null);
    }

    @Test
    public void MercenaryGoal() {
        DungeonManiaController dungeonManiaController = new DungeonManiaController();
        dungeonManiaController.newGame("basicmap8", "Standard");

        // pick up the treasure on (2, 1)
        dungeonManiaController.tick(null, Direction.RIGHT);
        // go to (3, 1) with mercenary now on (4, 1)
        dungeonManiaController.tick(null, Direction.RIGHT);
        // find the mercenary
        DungeonMania game = dungeonManiaController.getLoadedGame();
        List<Entity> entities = game.getEntities();

        String id = "";
        int spider = 0;
        for (Entity e : entities) {
            if (e instanceof Mercenary) {
                id = e.getId();
            }
            if(e instanceof Spider) {
                spider++;
            }
        }

        // bribe the mercenary
        dungeonManiaController.interact(id);
        // game should now be complete
        if(spider == 0) {
        assertTrue(dungeonManiaController.getLoadedGame() == null);
        }
        else {
        assertFalse(dungeonManiaController.getLoadedGame() == null);
        }
    }
}
