package dungeonmania;

import java.util.List;

public class TreasureGoal extends GoalLeaf {
    public TreasureGoal() {
        super("treasure");
    }

    @Override
    public Boolean isComplete(DungeonMania game) {
        List<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity instanceof TreasureEntity) {
                return false;
            }
        }
        return true;
    }
}
