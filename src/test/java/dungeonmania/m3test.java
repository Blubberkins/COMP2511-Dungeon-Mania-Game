package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.*;

import java.util.List;

public class m3test {
    @Test
    public void testAssassinSpawn() {
        int numAssassins = 0;
        int numTrials = 10000;
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

        // given 10000 bernoulli trials
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
    public void testHydra() {
        int numIncreases = 0;
        int numTrials = 10000;
        for (int i = 0; i < numTrials; i++) {
            DungeonManiaController dm = new DungeonManiaController();
            DungeonMania game = null;

            int spider = -1;
            while (spider != 0) {
                int spidercount = 0;
                dm.newGame("hydratest", "Hard");
                game = dm.getLoadedGame();
                List<Entity> entities = game.getEntities();
                for (Entity e : entities) {
                    if (e instanceof Spider) {
                        spidercount++;
                    }
                }
                spider = spidercount;
            }

            dm.tick(null, Direction.RIGHT);
            dm.tick(null, Direction.RIGHT);
            assertDoesNotThrow(() -> {
                DungeonMania currGame = dm.getLoadedGame();
                dm.interact(findMercenary(currGame).getId());
            });

            assertTrue(game.getCharacter().getAllies().size() == 1);

            // wait 48 more ticks, the hydra should spawn on the 50th tick
            for (int j = 0; j < 24; j++) {
                dm.tick(null, Direction.LEFT);
                dm.tick(null, Direction.RIGHT);
            }

            Entity hydra = findHydra(game);
            assertTrue(hydra != null);
            assertTrue(hydra instanceof MovingEntity);
            int hydraHP = ((MovingEntity) hydra).getHealth();

            int currHP = hydraHP;
            // do stuff until the hydra comes into a single combat with the player
            while (currHP == hydraHP) {
                dm.tick(null, Direction.UP);
                currHP = ((MovingEntity) hydra).getHealth();
            }

            if (currHP > hydraHP) {
                numIncreases += 1;
            }
        }

        // similar to assassin test, given 10000 bernoulli trials
        // we want to test the hypothesis that p = 0.5
        // the alternate hypothesis is that p =/= 0.5
        // z score (0.05, two tailed test) is 1.96
        double xBar = (double) numIncreases / numTrials;
        double stdev = Math.sqrt(0.5 * 0.5); // sqrt(p(1-p))

        // normal approximation
        double testStatistic = Math.abs((xBar - 0.5) / (stdev / Math.sqrt(numTrials)));

        // p value in (-1.96, 1.96) using normal approximation
        // we shouldn't be able to reject the null hypothesis
        // by law of large numbers the result should be close enough to E(X) anyway
        assertTrue(testStatistic < 1.96);
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
        for (int i = 0; i < 5; i++) {
            dm.tick(null, Direction.RIGHT);
        }

        // even after the player opens the door, they should still have the stone
        inventory = game.getItems();
        assertTrue(inventory.size() == 1);
    }

    @Test
    public void testAnduril() {
        Boolean hasAssassin = true;
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        while (hasAssassin) {
            int spider = -1;
            while (spider != 0) {
                int spidercount = 0;
                dm.newGame("hydratest2", "Hard");
                game = dm.getLoadedGame();
                List<Entity> entities = game.getEntities();
                for (Entity e : entities) {
                    if (e instanceof Spider) {
                        spidercount++;
                    }
                }
                spider = spidercount;
                // break the loop if we have not spawned an assassin
                // we want to compare damage of anduril on non-bosses
                // so we 100% do NOT want an assassin
                if (!(findMercenary(game) instanceof Assassin)) {
                    hasAssassin = false;
                }
            }
        }

        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);
        // should now enter combat with the mercenary

        Mercenary m = findMercenary(game);
        Boolean hasArmour = m.HasArmour();
        int mHP = m.getHealth();

        dm.tick(null, Direction.NONE);
        int nonBossDMG = mHP - m.getHealth();

        // tick 47 more times until a hydra spawns
        for (int i = 0; i < 47; i++) {
            dm.tick(null, Direction.NONE);
        }

        Entity hydra = findHydra(game);
        assertTrue(hydra != null);
        int hydraHP = ((MovingEntity) hydra).getHealth();

        int currHP = hydraHP;
        // do stuff until the hydra comes into a single combat with the player
        while (currHP == hydraHP) {
            dm.tick(null, Direction.NONE);
            currHP = ((MovingEntity) hydra).getHealth();
        }

        int BossDMG = hydraHP - currHP;
        // armour will cause damage reduction to the mercenary, but otherwise the
        // mercenary should take 1/3 the damage
        // chose boss / 3 rather than non boss * 3 to account for possible rounding

        if (hasArmour) {
            assertTrue(BossDMG > nonBossDMG);
        } else {
            assertTrue(BossDMG / 3 == nonBossDMG);
        }

        // hydra will never regen a head
        assertTrue(currHP < hydraHP);
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

    public Entity findHydra(DungeonMania game) {
        List<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity.getType().compareTo("hydra") == 0) {
                return entity;
            }
        }

        return null;
    }
}
