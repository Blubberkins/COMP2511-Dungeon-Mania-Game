package dungeonmania;

import dungeonmania.util.Position;

public class SunStone extends KeyEntity {    
    public SunStone(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsUsed(false);
    }
}