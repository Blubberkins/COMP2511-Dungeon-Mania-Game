package dungeonmania;

import dungeonmania.util.Position;

public class SwordEntity extends Weapons {
    Damage weapon;

    public SwordEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setDurability(4);
    }

    @Override
    public double calculateDamage() {
        return 5.0 + weapon.calculateDamage();
    }

}
