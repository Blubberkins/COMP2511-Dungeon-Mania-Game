package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.List;

// By Liam

public class m3testPart2 {

    @Test
    public void testHydra() {
        int numIncreases = 0;
        int numTrials = 2500;
        for (int i = 0; i < numTrials; i++) {
            DungeonManiaController dm = new DungeonManiaController();
            DungeonMania game = null;

            Boolean hasAssassin = true;
            while (hasAssassin) {
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

                if (!(findMercenary(game) instanceof Assassin)) {
                    hasAssassin = false;
                    // don't want complications when bribing w/o one ring
                }
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
                dm.getLoadedGame().getCharacter().setHealth(30);
                dm.tick(null, Direction.LEFT);
                dm.getLoadedGame().getCharacter().setHealth(30);
                dm.tick(null, Direction.RIGHT);
            }

            dm.tick(null, Direction.NONE);
            dm.getLoadedGame().getCharacter().setHealth(30);

            Hydra hydra = findHydra(game);
            assertTrue(hydra != null);
            int hydraHP = hydra.getHealth();

            int currHP = hydraHP;
            // do stuff until the hydra comes into a single combat with the player
            while (currHP == hydraHP) {
                dm.tick(null, Direction.RIGHT);
                currHP = hydra.getHealth();
            }

            if (currHP > hydraHP) {
                numIncreases += 1;
            }

            // for safety
            game.removeEntity(hydra);
        }

        // similar to assassin test, given 2500 bernoulli trials
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
            }

            // break the loop if we have not spawned an assassin
            // we want to compare damage of anduril on non-bosses
            // so we 100% do NOT want an assassin
            if (!(findMercenary(game) instanceof Assassin)) {
                hasAssassin = false;
            }
        }

        dm.tick(null, Direction.RIGHT);
        // should have anduril now
        assertTrue(game.getItems().get(0) instanceof Anduril);
        // make sure anduril doesn't break just in case the mercenary has armour
        ((Anduril) game.getItems().get(0)).setDurability(50);
        // preliminary set up
        Mercenary m = findMercenary(game);
        Boolean hasArmour = m.HasArmour();
        int mHP = m.getHealth();
        // should now enter combat with the mercenary after this move
        dm.tick(null, Direction.RIGHT);

        int nonBossDMG = mHP - m.getHealth();

        // tick 48 more times until a hydra spawns
        for (int i = 0; i < 48; i++) {
            dm.tick(null, Direction.NONE);
            game.getCharacter().setHealth(30); // so player doesn't die to mercenary
        }

        Hydra hydra = findHydra(game);
        assertTrue(hydra != null);
        int hydraHP = ((MovingEntity) hydra).getHealth();

        // make sure character's health is back at max of 30
        // just so we have the same conditions as when fighting the mercenary
        int currHP = hydraHP;
        // do stuff until the hydra comes into a single combat with the player
        while (currHP == hydraHP) {
            if (hydra.getPos().getX() > game.getCharacter().getPos().getX()) {
                dm.tick(null, Direction.RIGHT);
            }
            if (hydra.getPos().getX() < game.getCharacter().getPos().getX()) {
                dm.tick(null, Direction.LEFT);
            }
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

    @Test
    public void testMidnight() {
        // midnight armour test first
        Boolean hasArmour = false;
        Boolean hasAssassin = true;
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        Mercenary firstM = null;
        while (!hasArmour || hasAssassin) {
            int spider = -1;
            while (spider != 0) {
                int spidercount = 0;
                dm.newGame("midnightArmour", "Hard");
                game = dm.getLoadedGame();
                List<Entity> entities = game.getEntities();
                for (Entity e : entities) {
                    if (e instanceof Spider) {
                        spidercount++;
                    }
                }
                spider = spidercount;
                // break the loop if we spawn a mercenary with armour
                // this is so we can construct the midnight armour
            }

            firstM = findMercenary(game);
            if (firstM.HasArmour()) {
                hasArmour = true;
            }

            if (!(firstM instanceof Assassin)) {
                hasAssassin = false;
            }
        }

        // ensure that the mercenary in question
        // is the one we care about at this point in time
        // (there are two, one at (4, 0) one at (11, 0))
        assertTrue(firstM.getPos().equals(new Position(4, 0)));
        assertTrue(firstM.HasArmour());

        // second mercenary at (9, 0)
        // but right now we are in combat with the first mercenary
        // on (2, 0), having picked up the sun stone on (1, 0)
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);

        // to fix issue where the character dies, we'll increase
        // their damage and health
        // we're only checking that midnight armor does relatively more/makes them take
        // relatively less, and we need to break past the mercenary's armour
        game.getCharacter().setDamage(100);
        game.getCharacter().setHealth(50);

        int mHP = firstM.getHealth();
        int dmgGiven = 0;
        int cHP = game.getCharacter().getHealth();
        int dmgReceived = 0;
        for (int i = 0; i < 5; i++) {
            dm.tick(null, Direction.NONE);
            if (i == 0) {
                dmgGiven = mHP - firstM.getHealth();
                dmgReceived = cHP - game.getCharacter().getHealth();
            }
        }

        // the second mercenary should now be the only mercenary
        // it should now be on (4, 0)
        firstM = findMercenary(game);
        assertTrue(firstM.getPos().equals(new Position(4, 0)));

        dm.build("midnight_armour");
        List<Entity> inventory = game.getItems();
        Entity armour = inventory.get(inventory.size() - 1);
        // the last object in the inventory should be the midnight armour
        // this accounts for the off chance we get the one ring
        assertTrue(armour instanceof MidnightArmour);

        // now we move to (3, 0) and fight the mercenary
        dm.tick(null, Direction.RIGHT);
        // after a single round of combat
        // should take less damage and deal more damage

        mHP = firstM.getHealth();
        cHP = game.getCharacter().getHealth();

        dm.tick(null, Direction.RIGHT);
        assertTrue(mHP - firstM.getHealth() > dmgGiven);
        assertTrue(cHP - game.getCharacter().getHealth() < dmgReceived);
    }

    @Test
    public void TestTimeTravel() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.newGame("TimetravelTest", "Peaceful");
        dm.getLoadedGame();
        DungeonMania game = dm.getLoadedGame();
        int numberofEntities = game.getEntities().size();
        dm.tick(null, Direction.LEFT);
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.RIGHT);
        assert (game.getEntities().size() == numberofEntities - 1);
        dm.tick(null, Direction.LEFT);
        dm.tick(null, Direction.LEFT);
        dm.rewind(3);
        game = dm.getLoadedGame();
        assert (game.getEntities().size() == numberofEntities);
        dm.tick(null, Direction.RIGHT);
        dm.tick(null, Direction.LEFT);
        dm.tick(null, Direction.LEFT);
        assert (game.getEntities().size() == numberofEntities - 1);

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
