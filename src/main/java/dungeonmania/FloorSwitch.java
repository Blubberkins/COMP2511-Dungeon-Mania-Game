package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloorSwitch extends StaticEntity {
    private boolean isTriggered;

    public FloorSwitch(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }

    public boolean checkTriggered (List<Entity> entities) {
        for (Entity entity: entities) {
            if (entity.getPos().equals(this.getPos())) {
                if (entity instanceof Boulder) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTriggered() {
        return isTriggered;
    }

    public void setisTriggered(boolean isTriggered) {
        this.isTriggered = isTriggered;
    }
}
