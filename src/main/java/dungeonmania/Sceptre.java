package dungeonmania;

import dungeonmania.util.Position;

public class Sceptre extends BuildableEntity {
    public Sceptre(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }
}