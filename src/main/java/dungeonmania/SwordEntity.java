package dungeonmania;

import dungeonmania.util.Position;

public class SwordEntity extends Weapons {
    Damage weapon;

    public SwordEntity(Damage weapon, Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(4);
        this.weapon = weapon;
    }

    @Override
    public int calculateDamage() {
        return 5 + weapon.calculateDamage();
    }

}
