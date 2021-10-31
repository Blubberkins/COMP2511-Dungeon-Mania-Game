package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ZombieToast extends MovingEntity {
    private ArmourEntity armour;

    public ZombieToast(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        super.setHealth(50);
        super.setDamage(5);
        this.armour = ChanceOfArmour();
    }

    public ArmourEntity ChanceOfArmour() {
        if(ThreadLocalRandom.current().nextInt(0, 11) == 5) {
            return new ArmourEntity(null, "armour", "armour" + this.getId());
        }
        return null;
    }
    public void decrementArmourDurability() {
        this.armour.decrementDurability();
    }
    public ArmourEntity getArmour(){
        return this.armour;
    }
    public Boolean HasArmour() {
        return this.HasArmour() != null;
    }
    @Override
    public void move(DungeonMania d) {   
        Boolean isWall = true;
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        Direction random = null;
        Position p = null;
        while (isWall) {
            isWall = false;
            random = directions.get(ThreadLocalRandom.current().nextInt(0, 3));
            for (Entity entity: d.getEntities()) {
                if ((entity instanceof Wall) && this.getPos().translateBy(random).equals(entity.getPos())) {
                    isWall = true;
                }
            }
        }
        p = this.getPos().translateBy(random);
        this.setPos(p);
    }

    public void receiveDMG(int damage) {
        super.setHealth(super.getHealth() - damage);
    }
 
}