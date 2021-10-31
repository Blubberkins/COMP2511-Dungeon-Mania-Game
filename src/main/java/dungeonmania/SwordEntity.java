package dungeonmania;

import dungeonmania.util.Position;

public class SwordEntity extends CollectableEntities {

    private final int durability = 2;

    public SwordEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }


    public int getDurability() {
        return durability;
    }
    
}

