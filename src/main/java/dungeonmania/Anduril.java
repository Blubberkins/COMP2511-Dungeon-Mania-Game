package dungeonmania;

import dungeonmania.util.Position;

public class Anduril extends Weapons{

    public Anduril (Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(10);
        // add extra damage
    }
    
}

