package dungeonmania;

import dungeonmania.util.Position;

public class Shield extends Weapons {

    Damage weapon;

    public Shield(Damage weapon, Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(4);
        this.weapon = weapon;
    }

    @Override
    public int calculateDamage() {
        return weapon.calculateDamage() - 5;
    }
}
