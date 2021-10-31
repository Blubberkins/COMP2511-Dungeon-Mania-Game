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

    public Boolean isEnemy(Entity entity) {
        List<String> enemies = new ArrayList<String>();

        enemies.add("spider");
        enemies.add("zombie_toast_spawner");
        enemies.add("zombie_toast");

        if (enemies.contains(entity.getType())) {
            return true;
        }

        if (entity.getType().compareTo("mercenary") == 0 && ((Mercenary) entity).isHostile()) {
            return true;
        }

        return false;
    }
}
