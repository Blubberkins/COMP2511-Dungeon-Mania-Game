package dungeonmania;

import dungeonmania.util.Position;

public class SwordEntity extends CollectableEntities {

    public SwordEntity(Position pos, String type, String id) {
        super(pos, type, id);
        //TODO Auto-generated constructor stub
    }



    private final int durability = 2;



    public int getDurability() {
        return durability;
    }
    
}

