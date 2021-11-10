package dungeonmania;

import java.util.List;

public class Battles {
    public static enum BattleOutcome {
        CHARACTER_WINS, ENEMY_WINS, NEITHER
    }

    /**
     * Calculates the outcome of a battle between the character and an enemy.
     * 
     * @param character
     * @param entity
     * @param items
     * @return BattleOutcome
     */
    public static BattleOutcome Battle(Character character, MovingEntity entity, List<Entity> items) {
        int allyDamage = 0;
        for (MovingEntity ally : character.getAllies()) {
            ally.setInBattle(true);
            allyDamage = (ally.getDamage() + ally.getHealth()) / 10;
            entity.receiveDMG(allyDamage);
        }

        Boolean anduril = false;
        int characterDamage = (character.getDamage() + character.getHealth()) / 10;
        for (Entity item : items) {
            if (item instanceof SwordEntity) {
                characterDamage += 5;
                ((Weapons) item).decrementDurability();
                if (item instanceof Anduril) {
                    anduril = true;
                }
            }
            if (item instanceof Bow) {
                ((Weapons) item).decrementDurability();
                entity.receiveDMG(characterDamage);
            }
            if (item instanceof MidnightArmour) {
                characterDamage += 5;
            }
        }
        if (entity instanceof ZombieToast && ((ZombieToast) entity).HasArmour()) {
            characterDamage = characterDamage / 2;
            ((ZombieToast) entity).decrementArmourDurability();
        }
        if (entity instanceof Mercenary && ((Mercenary) entity).HasArmour()) {
            characterDamage = characterDamage / 2;
            ((Mercenary) entity).decrementArmourDurability();
        }
        if (entity instanceof Hydra || entity instanceof Assassin) {
            if (anduril) {
                characterDamage *= 3;
            }
        }

        if (!(entity instanceof Hydra && anduril)) {
            entity.receiveDMG(characterDamage);
        } else {
            int prev = entity.getHealth();
            entity.receiveDMG(characterDamage);
            int next = entity.getHealth();
            if (next > prev) {
                entity.setHealth(2 * prev - next);
            }
        }

        if (!entity.isAlive()) {
            character.setInBattle(false);
            for (MovingEntity ally : character.getAllies()) {
                ally.setInBattle(false);
            }
            return BattleOutcome.CHARACTER_WINS;
        }
        int enemydamage = (entity.getDamage() + entity.getHealth()) / 5;
        for (Entity item : items) {
            if (item instanceof Shield) {
                enemydamage -= 5;
                ((Weapons) item).decrementDurability();
            }
            if (item instanceof ArmourEntity) {
                ((Weapons) item).decrementDurability();
                enemydamage = enemydamage / 2;

            }
            if (item instanceof MidnightArmour) {
                enemydamage -= 5;
            }
        }
        character.receiveDMG(enemydamage);
        if (!character.isAlive()) {
            return BattleOutcome.ENEMY_WINS;
        }
        return BattleOutcome.NEITHER;
    }
}