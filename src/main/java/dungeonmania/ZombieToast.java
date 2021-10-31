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
    private boolean hasArmor;

    public ZombieToast(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        this.hasArmor = false;
        super.setHealth(50);
        super.setDamage(5);
    }

    public boolean isHasArmor() {
        return hasArmor;
    }

    public void setHasArmor(boolean hasArmor) {
        this.hasArmor = hasArmor;
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
            random = directions.get(ThreadLocalRandom.current().nextInt(0, 3));
            for (Entity entity: d.getEntities()) {
                if (!(entity instanceof Wall) && this.getPos().translateBy(random).equals(entity.getPos())) {
                    isWall = false;
                }
            }
        }
        p = this.getPos().translateBy(random);
        this.setPos(p);
    }

    public void receiveDMG(int damage) {
    }
 
}
