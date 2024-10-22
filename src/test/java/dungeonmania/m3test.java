package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.exceptions.InvalidActionException;

import java.util.List;

// By Liam

public class m3test {
    @Test
    public void testAssassinSpawn() {
        int numAssassins = 0;
        int numTrials = 2500;
        for (int i = 0; i < numTrials; i++) {
            // load the game with a mercenary
            DungeonManiaController dm = new DungeonManiaController();
            DungeonMania game = null;
            int spider = -1;
            while (spider != 0) {
                int spidercount = 0;
                dm.newGame("basicmap10", "Hard");
                game = dm.getLoadedGame();
                List<Entity> entities = game.getEntities();
                for (Entity e : entities) {
                    if (e instanceof Spider) {
                        spidercount++;
                    }
                }
                spider = spidercount;
            }

            if (findMercenary(game) instanceof Assassin) {
                numAssassins += 1;
            }
        }

        // given 2500 bernoulli trials
        // we want to test the hypothesis that p = 0.25
        // the alternate hypothesis is that p =/= 0.25
        // z score (0.05, two tailed test) is 1.96
        double xBar = (double) numAssassins / numTrials;
        double stdev = Math.sqrt(0.25 * 0.75); // sqrt(p(1-p))

        // normal approximation
        double testStatistic = Math.abs((xBar - 0.25) / (stdev / Math.sqrt(numTrials)));

        // p value in (-1.96, 1.96) using normal approximation
        // we shouldn't be able to reject the null hypothesis
        // by law of large numbers the result should be close enough to E(X) anyway
        assertTrue(testStatistic < 1.96);
    }

    @Test
    public void testFailedBribe() {
        Boolean hasAssassin = false;
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        while (!hasAssassin) {
            // load the game with a mercenary
            int spider = -1;
            while (spider != 0) {
                int spidercount = 0;
                dm.newGame("basicmap8", "Hard");
                game = dm.getLoadedGame();
                List<Entity> entities = game.getEntities();
                for (Entity e : entities) {
                    if (e instanceof Spider) {
                        spidercount++;
                    }
                }
                spider = spidercount;
            }

            // break the loop if we've spawned an assassin
            if (findMercenary(game) instanceof Assassin) {
                hasAssassin = true;
            }
        }

        String id = findMercenary(game).getId();

        game = dm.getLoadedGame();
        // should pick up the treasure
        dm.tick(null, Direction.RIGHT);
        // should be one tile away from the assassin, ie. bribeable distance
        dm.tick(null, Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> {
            dm.interact(id);
        });
    }

    @Test
    public void testDijkstra() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;

        int spider = -1;
        while (spider != 0) {
            int spidercount = 0;
            dm.newGame("pathingmap1", "Hard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if (e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
        }

        Mercenary m = findMercenary(game);
        // there are two paths to the player
        // we will make the player do nothing and observe where the mercenary goes
        // the mercenary should take the shortest path to the player on (5, 1)
        // the upper path
        dm.tick(null, Direction.NONE);
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(2, 1)));
    }

    @Test
    public void testDijkstraVariations() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;

        // loading a different map, same as above test but player swaps places
        // with the wall on (5, 3)
        int spider = -1;
        while (spider != 0) {
            int spidercount = 0;
            dm.newGame("pathingmap2", "Hard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if (e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
        }

        Mercenary m = findMercenary(game);
        // the mercenary should take the shortest path to the player on (5, 3)
        // the lower path, in this case
        dm.tick(null, Direction.NONE);
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(2, 3)));

        // extra checks
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(3, 3)));
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(4, 3)));
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(5, 3)));

        // loading yet another map. same as the map in testDijkstra
        // but there are now swamp tiles on the upper path
        // enough so that the mercenary should take the lower path
        spider = -1;
        while (spider != 0) {
            int spidercount = 0;
            dm.newGame("pathingmap3", "Hard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if (e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
        }

        m = findMercenary(game);
        dm.tick(null, Direction.NONE);
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(2, 3)));

        // extra checks
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(3, 3)));
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(4, 3)));
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(4, 2)));
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(4, 1)));
        dm.tick(null, Direction.NONE);
        assertTrue(m.getPos().equals(new Position(5, 1)));

    }

    @Test
    public void testDijkstraWall() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;

        // spawns an enclosed dungeon, which is a loop with a wall separating
        // player and mercenary
        int spider = -1;
        while (spider != 0) {
            int spidercount = 0;
            dm.newGame("dijkstraException", "Hard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if (e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
        }

        // mercenary starts opposite player, blocked by a wall
        Mercenary m = findMercenary(game);
        dm.tick(null, Direction.DOWN);
        // character has moved down, shortest path goes through a wall
        // so mercenary should ignore that and take the shortest path upwards
        Position pPos = game.getCharacter().getPos();
        Position expected = new Position(pPos.getX() + 2, pPos.getY() - 2);
        assertTrue(m.getPos().equals(expected));
    }

    @Test
    public void testNewInvincibility() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;

        // spawns an enclosed dungeon, which is a loop with a wall separating
        // player and mercenary
        int spider = -1;
        while (spider != 0) {
            int spidercount = 0;
            dm.newGame("dijkstraException", "Standard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if (e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
        }

        // hack an invicibility potion into the inventory for the purposes of this test
        game.AddItem("invincibility_potion");
        String id = game.getItems().get(0).getId();

        // mercenary starts opposite player, blocked by a wall
        Mercenary m = findMercenary(game);
        // make the player invincible
        dm.tick(id, Direction.NONE);
        assertTrue(game.getCharacter().getisInvincible());
        // character has stayed put, shortest path goes through a wall
        // so mercenary should stay put until the player moves
        Position pPos = game.getCharacter().getPos();
        Position expected = new Position(pPos.getX() + 2, pPos.getY());
        assertTrue(m.getPos().equals(expected));

        // checking one more tick, we move down, and so should the mercenary
        dm.tick(null, Direction.DOWN);
        expected = new Position(pPos.getX() + 2, pPos.getY() + 1);
        assertTrue(m.getPos().equals(expected));
    }

    @Test
    public void testSunStone() {
        Boolean hasAssassin = true;
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        while (hasAssassin) {
            // load the game with a mercenary
            int spider = -1;
            while (spider != 0) {
                int spidercount = 0;
                dm.newGame("sunstoneTest", "Hard");
                game = dm.getLoadedGame();
                List<Entity> entities = game.getEntities();
                for (Entity e : entities) {
                    if (e instanceof Spider) {
                        spidercount++;
                    }
                }
                spider = spidercount;
            }

            // break the loop if we have not spawned an assassin
            // just for simplicity's sake, we don't want to have the situation
            // where we run into an assassin and don't have the one ring
            if (!(findMercenary(game) instanceof Assassin)) {
                hasAssassin = false;
            }
        }

        String id = findMercenary(game).getId();

        // pick up the sunstone, then move next to the mercenary
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);

        assertDoesNotThrow(() -> {
            dm.interact(id);
        });

        // the player should still have the sun stone after bribing the mercenary
        List<Entity> inventory = game.getItems();
        assertTrue(inventory.size() == 1);
        Character player = game.getCharacter();
        assertTrue(player.getAllies().size() == 1);

        // there is a door on (7, 1), player should be at (3, 1) currently
        // there is also an exit on (8, 1)
        // player should be able to move successfully through the door
        for (int i = 0; i < 4; i++) {
            dm.tick(null, Direction.RIGHT);
        }

        // even after the player opens the door, they should still have the stone
        assertTrue(dm.getLoadedGame() != null);
        inventory = game.getItems();
        assertTrue(inventory.size() == 1);

        // for completion's sake
        dm.tick(null, Direction.RIGHT);
        assertEquals(dm.getLoadedGame(), null);
    }

    @Test
    public void testSceptre() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;

        int spider = -1;
        while (spider != 0) {
            int spidercount = 0;
            dm.newGame("sceptre", "Hard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if (e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;
        }

        // move to the right and pick up all the stuff
        // to build a sceptre
        for (int i = 0; i < 3; i++) {
            dm.tick(null, Direction.RIGHT);
        }

        dm.build("sceptre");
        // should now have a sceptre
        // assume also that the sun stone DOES disappear if you use it as a crafting
        // material
        // it doesn't disappear as usual if you use it otherwise
        List<Entity> inventory = game.getItems();
        assertTrue(inventory.size() == 1);
        assertTrue(inventory.get(0) instanceof Sceptre);

        // player should be on (3, 0)
        // mercenary on (4, 0)
        String id = findMercenary(game).getId();
        dm.interact(id);

        // should now be allied
        Character player = game.getCharacter();
        assertTrue(player.getAllies().size() == 1);

        // they should keep the sceptre
        inventory = game.getItems();
        assertTrue(inventory.size() == 1);
        assertTrue(inventory.get(0).getType().compareTo("sceptre") == 0);

        // after more than ten ticks, the mercenary should be hostile again
        for (int i = 0; i < 11; i++) {
            dm.tick(null, Direction.RIGHT);
        }

        assertTrue(player.getAllies().size() == 0);
    }

    public Mercenary findMercenary(DungeonMania game) {
        List<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity instanceof Mercenary) {
                return (Mercenary) entity;
            } else if (entity instanceof Assassin) {
                return (Assassin) entity;
            }
        }

        return null;
    }

    public Hydra findHydra(DungeonMania game) {
        List<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity instanceof Hydra) {
                return (Hydra) entity;
            }
        }

        return null;
    }
}
