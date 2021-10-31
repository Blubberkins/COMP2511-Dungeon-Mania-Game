package dungeonmania;

import dungeonmania.util.Position;

public class Weapons extends CollectableEntities{
    private int durability;
    public Weapons(Position pos, String type, String id) {
        super(pos, type, id);
        //TODO Auto-generated constructor stub
    }
    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void decrementDurability() {
        this.durability--;
    }

}
