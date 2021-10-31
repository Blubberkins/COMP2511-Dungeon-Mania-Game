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
    private Boolean isBribed;

    public Mercenary(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(true);
        super.setHealth(30);
        super.setDamage(5);
        this.isBribed = false;
    }

    public Boolean getIsBribed() {
        return isBribed;
    }

    public void setIsBribed(Boolean isBribed) {
        this.isBribed = isBribed;
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
                    optimal.add(Direction.DOWN);
                    optimal.add(Direction.UP);
                } else {
                    optimal.add(Direction.UP);
                    optimal.add(Direction.DOWN);
                }
                optimal.add(Direction.LEFT);
            } else {
                optimal.add(Direction.LEFT);
                if (yPos) {
                    optimal.add(Direction.DOWN);
                    optimal.add(Direction.UP);
                } else {
                    optimal.add(Direction.UP);
                    optimal.add(Direction.DOWN);
                }
                optimal.add(Direction.RIGHT);
            }
        } else {
            if (yPos) {
                optimal.add(Direction.DOWN);
                if (xPos) {
                    optimal.add(Direction.RIGHT);
                    optimal.add(Direction.LEFT);
                } else {
                    optimal.add(Direction.LEFT);
                    optimal.add(Direction.RIGHT);
                }
                optimal.add(Direction.UP);
            } else {
                optimal.add(Direction.UP);
                if (xPos) {
                    optimal.add(Direction.RIGHT);
                    optimal.add(Direction.LEFT);
                } else {
                    optimal.add(Direction.LEFT);
                    optimal.add(Direction.RIGHT);
                }
                optimal.add(Direction.DOWN);
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

    @Override
    public void receiveDMG(int damage) {
        super.setHealth(super.getHealth() - super.getDamage());

    }

    // public static void main(String args[]) {
    // Position pos1 = new Position(0, 0);
    // Position pos2 = new Position(3, 3);

    // Character c = new Character(pos1, "player", "hello1");
    // Mercenary m = new Mercenary(pos2, "mercenary", "hello2");
    // DungeonMania dungeonmania = new DungeonMania("Peaceful", "hello");

    // List<Entity> entities = new ArrayList<Entity>();
    // entities.add(c);
    // entities.add(m);
    // dungeonmania.setEntities(entities);

    // m.move(dungeonmania);
    // System.out.println(c.getPos());
    // System.out.println(m.getPos());

    // m.move(dungeonmania);
    // System.out.println(c.getPos());
    // System.out.println(m.getPos());
    // }
}
