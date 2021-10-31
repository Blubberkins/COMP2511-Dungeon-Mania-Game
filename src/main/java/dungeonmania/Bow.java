package dungeonmania;

import java.util.List;

import dungeonmania.util.Position;

public class Bow extends Weapons {

    public Bow(Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(4);
    }
    
}
