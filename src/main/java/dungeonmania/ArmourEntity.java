package dungeonmania;

import dungeonmania.util.Position;

public class ArmourEntity extends CollectableEntities {

    private final int durability = 2;

    public ArmourEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }

    public int getDurability() {
        return durability;
    }
    
}
