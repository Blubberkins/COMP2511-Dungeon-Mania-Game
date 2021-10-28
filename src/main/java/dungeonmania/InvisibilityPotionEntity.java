package dungeonmania;

import dungeonmania.util.Position;

public class InvisibilityPotionEntity extends CollectableEntities {
    
    public InvisibilityPotionEntity(Position pos, String type, String id) {
        super(pos, type, id);
        //TODO Auto-generated constructor stub
    }

    private final int durability = 5;
    private int time_remaining;


    public int getDurability() {
        return durability;
    }

    public int getTime_remaining() {
        return time_remaining;
    }

    public void setTime_remaining(int time_remaining) {
        this.time_remaining = time_remaining;
    }

}
