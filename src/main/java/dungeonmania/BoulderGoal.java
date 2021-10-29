package dungeonmania;

import java.util.List;
import java.util.ArrayList;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;

public class BoulderGoal extends GoalLeaf {
    public BoulderGoal() {
        super("boulders");
    }

    @Override
    public Boolean isComplete(DungeonMania game) {
        List<Entity> entities = game.getEntities();

        List<Entity> switches = new ArrayList<Entity>();

        for (Entity entity : entities) {
            if (entity.getType().compareTo("switch") == 0) {
                switches.add(entity);
            }
        }

        for (Entity switchEntity : switches) {
            if (!hasBoulder(game, switchEntity.getPos())) {
                return false;
            }
        }

        return true;
    }

    public static Boolean hasBoulder(DungeonMania game, Position position) {
        List<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity.getType().compareTo("boulder") == 0) {
                if (position.equals(entity.getPos())) {
                    return true;
                }
            }
        }
        return false;
    }
}
