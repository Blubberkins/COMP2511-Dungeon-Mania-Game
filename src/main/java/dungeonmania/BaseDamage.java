package dungeonmania;

import dungeonmania.util.Position;

public class BaseDamage extends Damage {

    private int base;

    public BaseDamage(Position pos, String type, String id) {
        super(pos, type, id);
        this.base = 1;
    }

    public BaseDamage(int base, Position pos, String type, String id) {
        super(pos, type, id);
        this.base = base;
    }

    @Override
    public double calculateDamage() {
        return this.base;
    }
}
