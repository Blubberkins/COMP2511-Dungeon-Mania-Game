package dungeonmania;

import java.util.concurrent.ThreadLocalRandom;

import dungeonmania.util.Position;

public class Hydra extends ZombieToast {

    public Hydra(Position pos, String type, String id) {
        super(pos, type, id);
        super.setHealth(40);
        super.setDamage(10);
    }

    @Override
    public void receiveDMG(int damage) {
        int chance = ThreadLocalRandom.current().nextInt(0, 1);
        if (chance == 0) {
            super.setHealth(super.getHealth() - damage);
        }
        else { // chance == 1
            super.setHealth(super.getHealth() + damage);
        }
    }
}