package dungeonmania;

import dungeonmania.util.Position;

public class BombEntity extends CollectableEntities {
    
    private final int radius = 1;
    public BombEntity(Position pos, String type, String id) {
        super(pos, type, id);
        //TODO Auto-generated constructor stub
    }

    public int getRadius() {
        return radius;
    }

}
