package dungeonmania;

import dungeonmania.util.Position;

public  abstract class StaticEntity extends Entity {
    public StaticEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }
}