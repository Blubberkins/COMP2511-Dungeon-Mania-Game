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

public class Boulder extends Wall {

    public Boulder(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }
    
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
