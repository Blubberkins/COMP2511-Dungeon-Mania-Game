package dungeonmania;

import dungeonmania.util.Position;

public class BuildableEntity extends CollectableEntities {
    public BuildableEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }
}