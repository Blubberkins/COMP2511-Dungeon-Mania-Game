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
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;

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

        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);
        assertDoesNotThrow(() -> {
            DungeonMania currGame = dm.getLoadedGame();
            dm.interact(findMercenary(currGame).getId());
        });

        assertTrue(game.getCharacter().getAllies().size() == 1);

        // TODO wait until a hydra spawns, then test the hydra's mechanic
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

}
