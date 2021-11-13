package dungeonmania;

import dungeonmania.util.Position;

public class ArmourEntity extends CollectableEntities {
    int durability;

    public ArmourEntity(Position pos, String type, String id) {
        super(pos, type, id);
        this.setDurability(4);
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getDurability() {
        return this.durability;
    }

}
