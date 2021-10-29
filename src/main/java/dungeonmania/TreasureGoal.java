package dungeonmania;

import java.util.List;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;

public class TreasureGoal extends GoalLeaf {
    public TreasureGoal() {
        super("treasure");
    }

    @Override
    public Boolean isComplete(DungeonResponse game) {
        List<EntityResponse> entities = game.getEntities();

        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("treasure") == 0) {
                return false;
            }
        }

        return true;
    }
}

