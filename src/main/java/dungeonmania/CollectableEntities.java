package dungeonmania;

import dungeonmania.util.Position;

public class CollectableEntities extends Entity {
    public CollectableEntities(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(true);
    }
   
}