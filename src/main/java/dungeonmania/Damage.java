package dungeonmania;

import dungeonmania.util.Position;

public abstract class Damage extends CollectableEntities {
    private int durability;
    private int base;

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

    public void setDamage(int base) {
        this.base = base;
    }

    public int getDamage() {
        return this.base;
    }

    public abstract int calculateDamage();
}
