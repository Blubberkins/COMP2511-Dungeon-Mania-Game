package dungeonmania.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import dungeonmania.DungeonMania;
import dungeonmania.SwampTile;
import dungeonmania.Entity;
import dungeonmania.Wall;

// By Liam

public class Dijkstra {
    public Dijkstra() {
        super();
    }

    public static Map<Position, Position> shortestPath(List<Position> grid, Position source, DungeonMania game) {
        Map<Position, Position> prev = new HashMap<Position, Position>();
        Map<Position, Double> dist = new HashMap<Position, Double>();

        Queue<Position> q = new LinkedList<Position>();

        if (!(grid.contains(source))) {
            return null; // can't generate if source doesn't exist
        }

        // setting up
        for (Position p : grid) {
            dist.put(p, Double.MAX_VALUE);
            prev.put(p, null);
            q.add(p);
        }

        dist.replace(source, (double) 0);

        while (!(q.isEmpty())) {
            Position curr = smallest(q, dist);
            if (curr == null) {
                break; // implies no other reachable tiles (walls or otherwise)
            }
            q.remove(curr);
            for (Position p : grid) {
                // note the only adjacent points are cardinally adjacent
                // we don't have the notion of a graph structure where points of
                // non-cardinal distance >= 1 and cardinal distance > 1 can be linked
                // we could have used absDistance(a, b) == 1, but same difference
                // note that distance will be counted as the no. of ticks to traverse
                // the square
                if (Position.isAdjacent(p, curr)) {
                    Double d = 1.0; // by default
                    if (game.getSwampTilePos().contains(p)) {
                        d = (double) game.getSlow(p);
                    }

                    if (!hasWall(game, p)) {
                        if (dist.get(curr) + d < dist.get(p) && q.contains(p)) {
                            dist.replace(p, dist.get(curr) + d);
                            prev.replace(p, curr);
                        }
                    }
                }
            }
        }

        return prev;
    }

    // finding the absolute cardinal distance (not euclidean distance!)
    public static int absDistance(Position a, Position b) {
        Position vector = Position.calculatePositionBetween(a, b);
        return Math.abs(vector.getX()) + Math.abs(vector.getY());
    }

    // finding the smallest distance in a queue that has elements as keys in a
    // distance map
    public static Position smallest(Queue<Position> q, Map<Position, Double> dist) {
        int size = q.size();
        Double smallestDist = Double.MAX_VALUE;
        Position smallest = null;
        for (int i = 0; i < size; i++) {
            Position curr = q.remove();
            if (dist.get(curr) < smallestDist) {
                smallest = new Position(curr.getX(), curr.getY());
                smallestDist = dist.get(curr);
            }
            q.add(curr); // we don't want to alter the queue
        }

        return smallest;
    }

    public static Boolean hasWall(DungeonMania game, Position position) {
        List<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity instanceof Wall && entity.getPos().equals(position)) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        DungeonMania game = new DungeonMania("Peaceful", "mydungeon");

        List<Position> grid = new ArrayList<Position>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                grid.add(new Position(i, j));
            }
        }

        game.addSwampTile(new SwampTile(new Position(1, 0)));

        Map<Position, Position> prev = shortestPath(grid, new Position(0, 0), game);
        for (Map.Entry<Position, Position> entry : prev.entrySet()) {
            System.out.println("-------------------------");
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
    }
}