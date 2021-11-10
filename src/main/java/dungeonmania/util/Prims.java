package dungeonmania.util;

import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.ThreadLocalRandom;

public class Prims {
    public List<List<Boolean>> generate(int width, int height, Position start, Position end) {
        List<List<Boolean>> grid = new ArrayList<>();
        List<Position> options = new ArrayList<Position>();

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
        // starting the indices from one and ending at one less than the boundary
        // ensures we never touch the boundary
        // inner check checks the distance and that said square is a wall
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                Position curr = new Position(i, j);
                if (twoAway(curr, start) && !grid.get(i).get(j)) {
                    options.add(curr);
                }
            }
        }

        while (!options.isEmpty()) {
            int index = ThreadLocalRandom.current().nextInt(0, options.size());
            Position next = options.get(index);
        }

        return grid;
    }

    public Boolean twoAway(Position a, Position b) {
        int x = a.getX() - b.getX();
        int y = a.getY() - b.getY();
        return Math.abs(x) + Math.abs(y) == 2;
    }
}

// TODO up to here before stopped working 

// let neighbours = each neighbour of distance 2 from next not on boundary that
// are empty
// if neighbours is not empty:
// let neighbour = random from neighbours
// maze[ next ] = empty (i.e. true)
// maze[ position inbetween next and neighbour ] = empty (i.e. true)
// maze[ neighbour ] = empty (i.e. true)

// add to options all neighbours of 'next' not on boundary that are of distance
// 2 away and are walls

// // at the end there is still a case where our end position isn't connected to
// the map
// // we don't necessarily need this, you can just keep randomly generating maps
// (was original intention)
// // but this will make it consistently have a pathway between the two.
// if maze[end] is a wall:
// maze[end] = empty

// if there are no cells in neighbours that are empty:
// // let's connect it to the grid
//
//
// maze[neighbour] = empty

//

// Or, in a more wordy fashion;

//
//
// Given a grid that consists of a 2D array of states (Wall/Empty) initialised
// to only walls
//

//
// Pick a random position from the list and a random cardinal neighbour of
// distance 2 that isn't on the boundary and is empty (not a wall)
// Pick a random neighbour that is a wall and connect the two via 2 empty spa
// Compute all cardinal positions that are wa

// Mark it as not a wall
//
// If it has atleast one neighbour that is a empty cell then don't do anyth

// You can presume:

// Start/End sit outside of the boundary.