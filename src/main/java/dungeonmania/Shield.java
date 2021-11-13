package dungeonmania;

import dungeonmania.util.Position;

public class Shield extends Weapons {

    Damage weapon;

    public Shield(Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(4);
    }

    @Override
    public double calculateDamage() {
        return weapon.calculateDamage() - 5.0;
    }
}
