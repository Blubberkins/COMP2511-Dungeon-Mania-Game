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
    public Boolean isComplete(DungeonResponse game) {
        List<EntityResponse> entities = game.getEntities();

        List<EntityResponse> switches = new ArrayList<EntityResponse>();

        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("switch") == 0) {
                switches.add(entity);
            }
        }

        for (EntityResponse switchEntity : switches) {
            if (!hasBoulder(game, switchEntity.getPosition())) {
                return false;
            }
        }

        return true;
    }

    public static Boolean hasBoulder(DungeonResponse game, Position position) {
        List<EntityResponse> entities = game.getEntities();

        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("boulder") == 0) {
                if (position.equals(entity.getPosition())) {
                    return true;
                }
            }
        }
        return false;
    }
}
