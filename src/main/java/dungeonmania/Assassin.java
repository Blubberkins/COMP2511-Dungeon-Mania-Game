package dungeonmania;

import dungeonmania.util.Position;

public class Assassin extends Mercenary {
    public Assassin(Position pos, String type, String id) {
        super(pos, type, id);
        super.setDamage(10); // add to assumptions assassin does double mercenaries damage
    }
}