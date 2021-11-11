package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Dijkstra;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class PrimsTest {
    @Test
    public void testGenerate() {
        DungeonManiaController dm = new DungeonManiaController();
        dm.generateDungeon(1, 1, 48, 48, "Peaceful");

        assertTrue(dm.getLoadedGame() != null);
    }

    @Test
    public void testCompleteGame() {
        DungeonManiaController dm = new DungeonManiaController();
        // simple map from top left to bottom right
        dm.generateDungeon(1, 1, 48, 48, "Peaceful");

        DungeonMania game = dm.getLoadedGame();
        Position source = new Position(1, 1);
        List<Position> grid = new ArrayList<Position>();

        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                grid.add(new Position(i, j));
            }
        }

        Map<Position, Position> shortestPaths = Dijkstra.shortestPath(grid, source, game);
        Stack<Position> path = tracePath(shortestPaths, source, new Position(48, 48));

        Character player = game.getCharacter();
        assertTrue(player != null);

        while (!path.isEmpty()) {
            Position next = path.pop();
            Direction toMove = findDirection(next, player.getPos());
            assertTrue(toMove != null);
            dm.tick(null, toMove);
        }

        assertTrue(dm.getLoadedGame() == null);
    }

    // same as above but with random starts and ends
    @Test
    public void moreTestCompletions() {
        for (int runs = 0; runs < 5; runs++) {
            DungeonManiaController dm = new DungeonManiaController();
            // simple map from top left to bottom right
            List<Integer> nums = new ArrayList<Integer>();
            Boolean same = true;
            while (same) {
                for (int i = 0; i < 4; i++) {
                    nums.add(ThreadLocalRandom.current().nextInt(1, 49));
                }
                if (!(nums.get(0) == nums.get(2) && nums.get(1) == nums.get(3))) {
                    same = false;
                }
            }

            dm.generateDungeon(nums.get(0), nums.get(1), nums.get(2), nums.get(3), "Peaceful");

            DungeonMania game = dm.getLoadedGame();
            Position source = new Position(nums.get(0), nums.get(1));
            List<Position> grid = new ArrayList<Position>();

            for (int i = 0; i < 50; i++) {
                for (int j = 0; j < 50; j++) {
                    grid.add(new Position(i, j));
                }
            }

            Map<Position, Position> shortestPaths = Dijkstra.shortestPath(grid, source, game);
            Stack<Position> path = tracePath(shortestPaths, source, new Position(nums.get(2), nums.get(3)));

            Character player = game.getCharacter();
            assertTrue(player != null);

            while (!path.isEmpty()) {
                Position next = path.pop();
                Direction toMove = findDirection(next, player.getPos());
                assertTrue(toMove != null);
                dm.tick(null, toMove);
            }

            assertTrue(dm.getLoadedGame() == null);
        }
    }

    // tracing the path given a shortest paths generated from dijkstra -- copied
    // from mercenary
    public Stack<Position> tracePath(Map<Position, Position> optimal, Position src, Position dest) {
        Stack<Position> path = new Stack<Position>();
        Position curr = dest;

        while (!(curr.equals(src))) {
            path.add(curr);
            curr = optimal.get(curr);

            if (curr == null) {
                break;
                // can't reach the src from dest
            }
        }

        return path;
    }

    // assuming you want to move from b to a
    public Direction findDirection(Position a, Position b) {
        int x = b.getX() - a.getX();
        int y = b.getY() - a.getY();
        if (x == 1 && y == 0) {
            return Direction.LEFT;
        } else if (x == -1 && y == 0) {
            return Direction.RIGHT;
        } else if (x == 0 && y == 1) {
            return Direction.UP;
        } else if (x == 0 && y == -1) {
            return Direction.DOWN;
        }
        return null;
    }
}
