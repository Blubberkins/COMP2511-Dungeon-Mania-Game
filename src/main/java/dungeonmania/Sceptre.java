package dungeonmania;

import dungeonmania.util.Position;

public class Sceptre extends Weapons {
    public Sceptre(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        super.setDurability(3); //sceptre can be used 3 times ?
    }
}