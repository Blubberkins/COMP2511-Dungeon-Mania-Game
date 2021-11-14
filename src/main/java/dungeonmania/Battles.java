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
    public static BattleOutcome BattleYourself(Character character, Character player, List<Entity> items,
            List<Entity> olditems) {
        int allyDamage = 0;
        for (MovingEntity ally : character.getAllies()) {
            ally.setInBattle(true);
            allyDamage = (ally.getDamage().calculateDamage() + ally.getHealth()) / 10;
            player.receiveDMG(allyDamage);
        }

        Boolean anduril = false;
        int characterDamage = (character.getDamage().calculateDamage() + character.getHealth()) / 10;
        Boolean hasBow = false;
        for (Entity item : items) {
            if (item instanceof SwordEntity) {
                ((Damage) item).decrementDurability();
                if (item instanceof Anduril) {
                    anduril = true;
                }
            }
            if (item instanceof Bow) {
                hasBow = true;
            }
            if (item instanceof MidnightArmour) {
                characterDamage += 5;
            }
        }
        if (hasBow) {
            findBow(items).decrementDurability();
        }
        int prev = player.getHealth();
        player.receiveDMG(characterDamage);
        int next = player.getHealth();
        if (next > prev) {
            player.setHealth(2 * prev - next);
        }

        if (!player.isAlive()) {
            character.setInBattle(false);
            for (MovingEntity ally : character.getAllies()) {
                ally.setInBattle(false);
            }
            return BattleOutcome.CHARACTER_WINS;
        }
        int enemydamage = (player.getDamage().calculateDamage() + player.getHealth()) / 5;
        double multiplier = 1.0;
        for (Entity item : items) {
            if (item instanceof ArmourEntity) {
                ((ArmourEntity) item).setDurability(((ArmourEntity) item).getDurability() - 1);
                multiplier = multiplier / 2;
            }
        }
        character.receiveDMG((int) Math.floor(enemydamage * multiplier));
        if (!character.isAlive()) {
            return BattleOutcome.ENEMY_WINS;
        }
        return BattleOutcome.NEITHER;
    }

    public static BattleOutcome Battle(Character character, MovingEntity entity, List<Entity> items) {
        int allyDamage = 0;
        for (MovingEntity ally : character.getAllies()) {
            ally.setInBattle(true);
            allyDamage = (ally.getDamage().calculateDamage() + ally.getHealth()) / 10;
            entity.receiveDMG(allyDamage);
        }

        Boolean anduril = false;
        int characterDamage = (character.getDamage().calculateDamage() + character.getHealth()) / 10;
        Boolean hasBow = false;
        for (Entity item : items) {
            if (item instanceof SwordEntity) {
                ((Damage) item).decrementDurability();
                if (item instanceof Anduril) {
                    anduril = true;
                }
            }
            if (item instanceof Bow) {
                hasBow = true;
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
            if (hasBow) {
                findBow(items).decrementDurability();
                entity.receiveDMG(characterDamage);
            }
        } else {
            if (hasBow) {
                findBow(items).decrementDurability();
            }
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
        int enemydamage = (entity.getDamage().calculateDamage() + entity.getHealth()) / 5;
        double multiplier = 1.0;
        for (Entity item : items) {
            if (item instanceof ArmourEntity) {
                ((ArmourEntity) item).setDurability(((ArmourEntity) item).getDurability() - 1);
                multiplier = multiplier / 2;

            }
        }
        character.receiveDMG((int) Math.floor(enemydamage * multiplier));
        if (!character.isAlive()) {
            return BattleOutcome.ENEMY_WINS;
        }
        return BattleOutcome.NEITHER;
    }   

    public static Bow findBow(List<Entity> items) {
        for (Entity e : items) {
            if (e instanceof Bow) {
                return (Bow) e;
            }
        }
        return null;
    }
}