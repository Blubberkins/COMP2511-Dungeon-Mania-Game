package dungeonmania;

import dungeonmania.util.Position;

public class SwordEntity extends Weapons{

    public SwordEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(4);
    }
    
}

