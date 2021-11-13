package dungeonmania;

import java.util.List;
import java.util.ArrayList;

public class EnemyGoal extends GoalLeaf {
    public EnemyGoal() {
        super("enemies");
    }

    @Override
    public Boolean isComplete(DungeonMania game) {
        List<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (isEnemy(entity)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a given entity is an enemy
     * 
     * @param entity
     * @return boolean
     */
    public Boolean isEnemy(Entity entity) {
        if (entity instanceof MovingEntity && !(entity instanceof Mercenary) && !(entity instanceof Character)) {
            return true;
        }

        if (entity instanceof ZombieToastSpawner) {
            return true;
        }

        if (entity instanceof Mercenary) {
            if (!((Mercenary) entity).getIsBribed()) {
                return true;
            }
        }
        return false;
    }
}
