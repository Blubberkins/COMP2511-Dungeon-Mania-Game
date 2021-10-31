package dungeonmania;

import dungeonmania.util.Position;

public class Exit extends StaticEntity {
    public Exit(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }
}
