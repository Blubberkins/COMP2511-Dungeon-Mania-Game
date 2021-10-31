package dungeonmania;

import dungeonmania.util.Position;

public class ArmourEntity extends Weapons {

    private final int durability = 2;

    public ArmourEntity(Position pos, String type, String id) {
        super(pos, type, id);
        this.setDurability(4);
    }   
}
