package dungeonmania;

import dungeonmania.util.Position;

public abstract class Weapons extends Damage {
    public Weapons(Position pos, String type, String id) {
        super(pos, type, id);
    }
}
