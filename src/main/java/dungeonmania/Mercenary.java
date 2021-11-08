package dungeonmania;

import dungeonmania.util.Dijkstra;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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

        // based on the map, decide which direction is best to move in
        // using dijkstra's algorithm
        Map<Position, Position> optimal = Dijkstra.shortestPath(mapPositions(dungeonmania), this.getPos(),
                dungeonmania);

        // TODO trace the optimal path, then make the move
        Stack<Position> path = tracePath(optimal, this.getPos(), getPlayer(dungeonmania).getPos());

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

    public List<Position> mapPositions(DungeonMania game) {
        List<Position> grid = new ArrayList<Position>();
        // TODO: return all positions in the grid
        return grid;
    }

    public Stack<Position> tracePath(Map<Position, Position> optimal, Position src, Position dest) {
        Stack<Position> path = new Stack<Position>();

        path.add(dest);

        return path;
    }

}
