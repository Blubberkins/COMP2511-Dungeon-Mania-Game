package dungeonmania;

import dungeonmania.util.Position;

public class Anduril extends SwordEntity {

    Weapons weapon;

    public Anduril(Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(10);
    }

    @Override
    public double calculateDamage() {
        return 5.0 + weapon.calculateDamage();
    }
}
