package dungeonmania;

import dungeonmania.util.Position;

public abstract class Damage extends CollectableEntities {
    private int durability;

    public Damage(Position pos, String type, String id) {
        super(pos, type, id);
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

    public abstract double calculateDamage();
}
