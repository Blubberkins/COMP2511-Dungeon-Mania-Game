package dungeonmania;

import dungeonmania.util.Position;

public class ArmourEntity extends Weapons {

    Weapons weapon;

    public ArmourEntity(Position pos, String type, String id) {
        super(pos, type, id);
        this.setDurability(4);
    }

    @Override
    public double calculateDamage() {
        return weapon.calculateDamage() / 2;
    }
}
