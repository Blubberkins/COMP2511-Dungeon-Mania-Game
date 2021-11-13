package dungeonmania;

import dungeonmania.util.Position;

public class Assassin extends Mercenary {
    private Damage damage;

    public Assassin(Position pos, String type, String id) {
        super(pos, type, id);
        this.damage = new BaseDamage(10, null, null, null);
        // add to assumptions assassin does double mercenary's damage
    }

    public void setDamage(int damage) {
        this.damage.setDamage(damage);
    }

    public Damage getDamage() {
        return this.damage;
    }
}
