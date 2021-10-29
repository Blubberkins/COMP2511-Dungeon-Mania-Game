package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;
import dungeonmania.response.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// By Liam

public class Mercenary extends MovingEntity {
    private final int minBribe = 1;

    public Mercenary(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }

    public int getBribe() {
        return this.minBribe;
    }

    public void move(DungeonMania dungeonmania) {
        Entity player = getPlayer(dungeonmania);
        // if the player doesn't exist, we do nothing
        if (player == null) {
            return;
        }

        // get the position vector
        Position vector = Position.calculatePositionBetween(this.getPos(), player.getPos());

        // based on the vector, decide which direction is best to move in
        List<Direction> order = optimalMove(vector, dungeonmania);

        // pick the single best move that is valid
        for (Direction move : order) {
            if (validMove(this.getPos(), move, dungeonmania)) {
                this.setPos(this.getPos().translateBy(move));
                break;
            }
        }
    }

    public Boolean validMove(Position position, Direction direction, DungeonMania dungeonMania) {
        Position newPos = position.translateBy(direction);

        List<Entity> entities = dungeonMania.getEntities();

        for (Entity entity : entities) {
            if (entity instanceof Wall && entity.getPos().equals(newPos)) {
                return false;
            }
        }

        return true;
    }

    public List<Direction> optimalMove(Position vector, DungeonMania dungeonMania) {
        Boolean xPos = vector.getX() >= 0;
        int xDist = Math.abs(vector.getX());
        Boolean yPos = vector.getY() >= 0;
        int yDist = Math.abs(vector.getY());

        List<Direction> optimal = new ArrayList<Direction>();

        if (xDist >= yDist) {
            if (xPos) {
                optimal.add(Direction.RIGHT);
                if (yPos) {
                    optimal.add(Direction.UP);
                    optimal.add(Direction.DOWN);
                } else {
                    optimal.add(Direction.DOWN);
                    optimal.add(Direction.UP);
                }
                optimal.add(Direction.LEFT);
            } else {
                optimal.add(Direction.LEFT);
                if (yPos) {
                    optimal.add(Direction.UP);
                    optimal.add(Direction.DOWN);
                } else {
                    optimal.add(Direction.DOWN);
                    optimal.add(Direction.UP);
                }
                optimal.add(Direction.RIGHT);
            }
        } else {
            if (yPos) {
                optimal.add(Direction.UP);
                if (xPos) {
                    optimal.add(Direction.RIGHT);
                    optimal.add(Direction.LEFT);
                } else {
                    optimal.add(Direction.LEFT);
                    optimal.add(Direction.RIGHT);
                }
                optimal.add(Direction.DOWN);
            } else {
                optimal.add(Direction.DOWN);
                if (xPos) {
                    optimal.add(Direction.RIGHT);
                    optimal.add(Direction.LEFT);
                } else {
                    optimal.add(Direction.LEFT);
                    optimal.add(Direction.RIGHT);
                }
                optimal.add(Direction.UP);
            }
        }

        return optimal;
    }

    public Entity getPlayer(DungeonMania dungeonmania) {
        List<Entity> entities = dungeonmania.getEntities();

        Entity player = null;

        for (Entity entity : entities) {
            if (entity.getType().compareTo("player") == 0) {
                player = entity;
                break;
            }
        }

        return player;
    }
}
