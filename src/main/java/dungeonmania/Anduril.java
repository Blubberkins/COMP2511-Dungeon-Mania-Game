package dungeonmania;

import dungeonmania.util.Position;

public class Anduril extends SwordEntity {

    Damage weapon;

    public Anduril(Damage weapon, Position pos, String type, String id) {
        super(weapon, pos, type, id);
        super.setDurability(10);
        this.weapon = weapon;
    }

    @Override
    public int calculateDamage() {
        return 5 + weapon.calculateDamage();
    }
}
