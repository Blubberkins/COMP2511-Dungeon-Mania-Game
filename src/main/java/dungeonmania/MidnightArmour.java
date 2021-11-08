package dungeonmania;

import dungeonmania.util.Position;

public class MidnightArmour extends Weapons {
    public MidnightArmour(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        super.setDurability(10); //midnightarmour can be used 10 times ?
    }
}