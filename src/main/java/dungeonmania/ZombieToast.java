package dungeonmania;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ZombieToast extends MovingEntity {
    private ArmourEntity armour;
    private Damage damage;

    public ZombieToast(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        super.setHealth(20);
        this.damage = new BaseDamage(5, null, null, null);
        this.armour = ChanceOfArmour();
    }

    /**
     * Generates a chance for a zombie to spawn with armor
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
     * Decrements armor durability
     */
    public void decrementArmourDurability() {
        this.armour.setDurability(this.armour.getDurability() - 1);
    }

    /**
     * Gets current zombies armour
     * 
     * @return ArmourEntity
     */
    public ArmourEntity getArmour() {
        return this.armour;
    }

    /**
     * Checks if a zombie has armor
     * 
     * @return boolean
     */
    public Boolean HasArmour() {
        return this.armour != null;
    }

    public void setDamage(int damage) {
        this.damage.setDamage(damage);
    }

    public Damage getDamage() {
        return this.damage;
    }

    /**
     * Generates random movement for zombie toasts and moves the zombie
     */
    @Override
    public void move(DungeonMania d) {
        List<Direction> directions = new ArrayList<>();

        if (!hasWall(d, this.getPos().translateBy(Direction.RIGHT))) {
            directions.add(Direction.RIGHT);
        }
        if (!hasWall(d, this.getPos().translateBy(Direction.LEFT))) {
            directions.add(Direction.LEFT);
        }
        if (!hasWall(d, this.getPos().translateBy(Direction.UP))) {
            directions.add(Direction.UP);
        }
        if (!hasWall(d, this.getPos().translateBy(Direction.DOWN))) {
            directions.add(Direction.DOWN);
        }

        Direction random = directions.get(ThreadLocalRandom.current().nextInt(0, directions.size()));
        this.setPos(this.getPos().translateBy(random));
    }

    /**
     * Does damage to the zombie
     */
    public void receiveDMG(int damage) {
        super.setHealth(super.getHealth() - damage);
    }

    public Boolean hasWall(DungeonMania game, Position pos) {
        for (Entity entity : game.getEntities()) {
            if ((entity instanceof Wall) && pos.equals(entity.getPos())) {
                return true;
            }
        }
        return false;
    }
}
