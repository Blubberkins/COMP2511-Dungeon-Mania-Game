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
     * @param entity
     * @return boolean
     */
    public Boolean isEnemy(Entity entity) {
        List<String> enemies = new ArrayList<String>();

        enemies.add("spider");
        enemies.add("zombie_toast_spawner");
        enemies.add("zombie_toast");

        if (enemies.contains(entity.getType())) {
            return true;
        }

        if (entity.getType().compareTo("mercenary") == 0) {
            if (!((Mercenary) entity).getIsBribed()) {
                return true;
            }
        }
        return false;
    }
}
