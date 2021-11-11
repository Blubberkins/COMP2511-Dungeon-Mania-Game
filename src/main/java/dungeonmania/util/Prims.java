package dungeonmania.util;

import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.ThreadLocalRandom;

public class Prims {
    public static List<List<Boolean>> generate(int width, int height, Position start, Position end) {
        List<List<Boolean>> grid = new ArrayList<>();

        // initialise grid to all walls
        for (int i = 0; i < height; i++) {
            grid.add(new ArrayList<Boolean>());
            for (int j = 0; j < width; j++) {
                grid.get(i).add(false);
            }
        }

        // make the starting point empty space
        int x = start.getX();
        int y = start.getY();
        grid.get(x).set(y, true);

        // add to options all neighbours of 'start' not on boundary that are of distance
        // 2 away and are walls

        List<Position> options = generateNeighbours(grid, start, 2, true);

        while (!options.isEmpty()) {
            int index = ThreadLocalRandom.current().nextInt(0, options.size());
            Position next = options.remove(index);

            List<Position> neighbours = generateNeighbours(grid, next, 2, false);
            if (!neighbours.isEmpty()) {
                int neighbourIndex = ThreadLocalRandom.current().nextInt(0, neighbours.size());
                Position neighbour = neighbours.get(neighbourIndex);
                Position between = between(next, neighbour);
                grid.get(next.getX()).set(next.getY(), true);
                grid.get(between.getX()).set(between.getY(), true);
                grid.get(neighbour.getX()).set(neighbour.getY(), true);
            }

            for (Position p : generateNeighbours(grid, next, 2, true)) {
                Boolean bool = grid.get(p.getX()).get(p.getY());
                if (!bool) {
                    options.add(p);
                }
            }
        }

        if (!grid.get(end.getX()).get(end.getY())) {
            grid.get(end.getX()).set(end.getY(), true);
            List<Position> endNeighbours = generateNeighbours(grid, end, 1, true);

            Boolean noEmpty = false;
            for (Position p : endNeighbours) {
                Boolean b = grid.get(p.getX()).get(p.getY());
                noEmpty = (noEmpty || b);
            }

            if (!noEmpty && endNeighbours.size() > 0) { // every p checked would have been false
                int endNeighbourIndex = ThreadLocalRandom.current().nextInt(0, endNeighbours.size());
                Position endNeighbour = endNeighbours.get(endNeighbourIndex);
                grid.get(endNeighbour.getX()).set(endNeighbour.getY(), true);
            }
        }

        return grid;
    }

    public static Boolean cardinalAway(Position a, Position b, int amt) {
        int x = a.getX() - b.getX();
        int y = a.getY() - b.getY();
        return (Math.abs(x) == amt) || (Math.abs(y) == amt);
    }

    public static List<Position> generateNeighbours(List<List<Boolean>> grid, Position src, int dist, Boolean isWall) {
        // starting the indices from one and ending at one less than the boundary
        // ensures we never touch the boundary
        // inner check checks the distance and that said square is a wall

        // assumes rectangular, at least height 1
        int height = grid.size();
        int width = grid.get(0).size();

        List<Position> neighbours = new ArrayList<Position>();

        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                Position curr = new Position(i, j);
                if (cardinalAway(curr, src, dist) && grid.get(i).get(j) != isWall) {
                    neighbours.add(curr);
                }
            }
        }

        return neighbours;
    }

    public static Position between(Position a, Position b) {
        // assumes we take a and b as two positions cardinal distance two apart
        Position between = null;
        if (Math.abs(b.getX() - a.getX()) % 2 == 0) {
            int newX = (a.getX() + b.getX()) / 2;
            int newY = (a.getY() + b.getY()) / 2;
            between = new Position(newX, newY);
        } else {
            between = new Position(a.getX(), b.getY());
        }
        return between;
    }
}
