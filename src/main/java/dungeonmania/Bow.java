package dungeonmania;

import dungeonmania.util.Position;

public class Bow extends Weapons {

    Damage weapon;

    public Bow(Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(4);
    }

    @Override
    public double calculateDamage() {
        return 2 * weapon.calculateDamage();
    }

}
