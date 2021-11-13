package dungeonmania;

import java.util.List;

import dungeonmania.util.Position;

public class ExitGoal extends GoalLeaf {
    public ExitGoal() {
        super("exit");
    }

    @Override
    public Boolean isComplete(DungeonMania game) {
        List<Entity> entities = game.getEntities();

        Boolean exitExists = false;
        for (Entity entity : entities) {
            if (entity instanceof Exit) {
                exitExists = true;
                Position exitposition = entity.getPos();
                if (exitposition.equals(getPlayerPosition(game))) {
                    return true;
                }
            }
        }

        if (!exitExists) {
            return true;
        }

        return false;
    }

    /**
     * Gets the current players position in the given game
     * 
     * @param game
     * @return Position
     */
    public Position getPlayerPosition(DungeonMania game) {
        List<Entity> entities = game.getEntities();

        Position position = null;
        for (Entity entity : entities) {
            if (entity.getType().compareTo("player") == 0) {
                position = entity.getPos();
                return position;
            }
        }

        return null;
    }
}
