package dungeonmania;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.List;

public class Boulder extends Wall {

    public Boulder(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }
    
    /**
     * Checks if the current instance of Boulder
     * can be moved, else it is blocked by a
     * wall or another boulder
     * @param entities
     * @param move
     * @return boolean
     */
    public boolean checkBoulderMovable (List<Entity> entities, Direction move) {
        for (Entity checkEmpty: entities) {
            if (checkEmpty.getPos().equals(this.getPos().translateBy(move))) {
                if (checkEmpty instanceof Wall) {
                    return false;
                }
            }
        }
        return true;
    }
}
