package dungeonmania;

import dungeonmania.util.Position;

import java.util.List;

public class FloorSwitch extends StaticEntity {
    private boolean isTriggered;

    public FloorSwitch(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }

    /**
     * Checks if there is a boulder on the same position
     * as the current instance of floorswitch, if there is
     * then it is triggered.
     * @param entities
     * @return
     */
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
