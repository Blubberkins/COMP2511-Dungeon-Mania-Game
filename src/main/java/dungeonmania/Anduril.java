package dungeonmania;

import dungeonmania.util.Position;

public class Anduril extends SwordEntity {

    public Anduril (Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(10);
    }
}

