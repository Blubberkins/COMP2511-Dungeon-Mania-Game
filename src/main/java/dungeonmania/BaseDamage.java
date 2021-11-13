package dungeonmania;

import dungeonmania.util.Position;

public class BaseDamage extends Damage {

    public BaseDamage(Position pos, String type, String id) {
        super(pos, type, id);
        this.setDamage(1);
    }

    public BaseDamage(int base, Position pos, String type, String id) {
        super(pos, type, id);
        this.setDamage(base);
    }

    @Override
    public int calculateDamage() {
        return this.getDamage();
    }
}
