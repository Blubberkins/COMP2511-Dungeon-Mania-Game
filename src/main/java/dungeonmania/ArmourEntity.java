package dungeonmania;

import dungeonmania.util.Position;

public class ArmourEntity extends CollectableEntities {

    public ArmourEntity(Position pos, String type, String id) {
        super(pos, type, id);
    }


    private final int durability = 2;


    public int getDurability() {
        return durability;
    }
    
}
