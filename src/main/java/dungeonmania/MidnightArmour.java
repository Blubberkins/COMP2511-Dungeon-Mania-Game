package dungeonmania;

import dungeonmania.util.Position;

public class MidnightArmour extends BuildableEntity {
    public MidnightArmour(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }
}