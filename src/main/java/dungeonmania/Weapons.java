package dungeonmania;

import dungeonmania.util.Position;

public abstract class Weapons extends CollectableEntities {
    private int durability;
    private int damage_modif;

    public Weapons(Position pos, String type, String id) {
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

    public int getDamage() {
        return this.damage_modif;
    }

    public abstract double calculateDamage();
}
