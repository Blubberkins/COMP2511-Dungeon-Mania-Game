package dungeonmania;

import dungeonmania.util.Position;

public class SwordEntity extends Weapons{

    private final int durability = 2;

    public SwordEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(2);
        //TODO Auto-generated constructor stub
    }
    
}

