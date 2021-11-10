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
    private int ticksLeftOnBribe;

    public Mercenary(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(true);
        super.setHealth(30);
        super.setDamage(5);
        this.isBribed = false;
        this.armour = ChanceOfArmour();
        this.ticksLeftOnBribe = -1;
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

        // trace the optimal path, then make the move
        Stack<Position> path = tracePath(optimal, this.getPos(), getPlayer(dungeonmania).getPos());
        if (!path.isEmpty()) { // if not same position as player
            Position next = path.peek();

            Position opposite = Position.calculatePositionBetween(next, this.getPos());

            if (next != null) {
                if (!((Character) getPlayer(dungeonmania)).getisInvincible()) {
                    this.setPos(next);
                } else {
                    this.setPos(this.getPos().translateBy(opposite));
                }
            }
        }
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
        super.setHealth(super.getHealth() - damage);
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

        // fetching the largest bounds for the map
        int x = 0;
        int y = 0;
        List<Entity> entities = game.getEntities();
        for (Entity entity : entities) {
            int tmp = entity.getPos().getX();
            if (tmp > x) {
                x = tmp;
            }

            tmp = entity.getPos().getY();
            if (tmp > y) {
                y = tmp;
            }
        }

        // have to make the plus one adjustment
        // eg. if the bottom right most entity is at (4, 4)
        // we need to factor in (4, 4) as a square on the map
        for (int i = 0; i < x + 1; i++) {
            for (int j = 0; j < y + 1; j++) {
                grid.add(new Position(i, j));
            }
        }

        return grid;
    }

    public Stack<Position> tracePath(Map<Position, Position> optimal, Position src, Position dest) {
        Stack<Position> path = new Stack<Position>();

        Position curr = dest;

        while (!(curr.equals(src))) {
            path.add(curr);
            curr = optimal.get(curr);

            if (curr == null) {
                break;
                // can't reach the player. if this null is encountered, at the top of the stack
                // the mercenary stays put, otherwise it moves as far along as it can
            }
        }

        return path;
    }

    public int getTicksLeftOnBribe() {
        return this.ticksLeftOnBribe;
    }

    public void setTicksLeftOnBribe(int ticks) {
        this.ticksLeftOnBribe = ticks;
    }
}
