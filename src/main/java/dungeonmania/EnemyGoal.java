package dungeonmania;

import java.util.List;
import java.util.ArrayList;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;

public class EnemyGoal extends GoalLeaf {
    public EnemyGoal() {
        super("enemies");
    }

    @Override
    public Boolean isComplete(DungeonResponse game) {
        List<EntityResponse> entities = game.getEntities();

        for (EntityResponse entity : entities) {
            if (isEnemy(entity)) {
                return false;
            }
        }

        return true;
    }

    public Boolean isEnemy(EntityResponse entity) {
        List<String> enemies = new ArrayList<String>();

        enemies.add("spider");
        enemies.add("zombie_toast_spawner");
        enemies.add("zombie_toast");

        if (enemies.contains(entity.getType())) {
            return true;
        }

        if (entity.getType().compareTo("mercenary") == 0) {
            // TODO: is the mercenary still hostile
        }

        return false;
    }
}
