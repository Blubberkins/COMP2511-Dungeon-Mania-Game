package dungeonmania;

import java.util.List;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;

public class ExitGoal extends GoalLeaf {
    public ExitGoal() {
        super("exit");
    }

    @Override
    public Boolean isComplete(DungeonResponse game) {
        List<EntityResponse> entities = game.getEntities();

        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("exit") == 0) {
                Position exitposition = entity.getPosition();
                if (exitposition.equals(getPlayerPosition(game))) {
                    return true;
                }
            }
        }

        return false;
    }

    public Position getPlayerPosition(DungeonResponse game) {
        List<EntityResponse> entities = game.getEntities();

        Position position = null;
        for (EntityResponse entity : entities) {
            if (entity.getType().compareTo("player") == 0) {
                position = entity.getPosition();
                return position;
            }
        }

        return null;
    }
}
