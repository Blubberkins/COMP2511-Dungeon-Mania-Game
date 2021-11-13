package dungeonmania;

import dungeonmania.util.Position;

public class Bow extends Weapons {

    Damage weapon;

    public Bow(Damage weapon, Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(4);
        this.weapon = weapon;
    }

    public Damage getWeapon() {
        return this.weapon;
    }

    @Override
    public int calculateDamage() {
        return 2 * weapon.calculateDamage();
    }

}
