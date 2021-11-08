package dungeonmania;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// By Liam

public class Mercenary extends MovingEntity {
    private final int minBribe = 1;
    private Boolean isBribed;
    private ArmourEntity armour;

    public Mercenary(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(true);
        super.setHealth(30);
        super.setDamage(5);
        this.isBribed = false;
        this.armour = ChanceOfArmour();
    }

    /**
     * Creates a random chance to generate armor
     * 
     * @return
     */
    public ArmourEntity ChanceOfArmour() {
        if (ThreadLocalRandom.current().nextInt(0, 11) == 5) {
            return new ArmourEntity(null, "armour", "armour" + this.getId());
        }
        return null;
    }

    /**
     * Moves the mercenary in a given game
     */
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
        if (!dungeonmania.getCharacter().getisInvincible()) {
            for (Direction move : order) {
                if (validMove(this.getPos(), move, dungeonmania)) {
                    this.setPos(this.getPos().translateBy(move));
                    break;
                }
            }
        } else {
            for (int i = 0; i < order.size(); i++) {
                if (validMove(this.getPos(), order.get(order.size() - 1 - i), dungeonmania)) {
                    this.setPos(this.getPos().translateBy(order.get(order.size() - 1 - i)));
                    break;
                }
            }
        }

    }

    /**
     * Checks if a desired move is valid (not blocked)
     * 
     * @param position
     * @param direction
     * @param dungeonMania
     * @return boolean
     */
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

    /**
     * Calculates the optimal movement
     * 
     * @param vector
     * @param dungeonMania
     * @return List<Direction>
     */
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

    /**
     * Gets the player in a game
     * 
     * @param dungeonmania
     * @return Entity
     */
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

    public ArmourEntity getArmour() {
        return this.armour;
    }

    public void decrementArmourDurability() {
        this.armour.decrementDurability();
    }

    public Boolean HasArmour() {
        return this.armour != null;
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

    public List<Position> mapPositions() {
        List<Position> grid = new ArrayList<Position>();
        // TODO: return all positions in the grid
        return grid;
    }

}
