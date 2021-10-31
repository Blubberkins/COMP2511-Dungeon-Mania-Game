package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.ChaCha20ParameterSpec;

import org.w3c.dom.CharacterData;

public class Battles {
    public static enum BattleOutcome {
        CHARACTER_WINS, ENEMY_WINS, NEITHER
    }
    public static BattleOutcome Battle(Character character, MovingEntity entity, List<Entity> items){
        int allyDamage = 0;
        for (MovingEntity ally: character.getAllies()){
            ally.setInBattle(true);
            allyDamage = (ally.getDamage() + ally.getHealth())/10;
            entity.receiveDMG(allyDamage);
        }

        int characterDamage = (character.getDamage() + character.getHealth())/10;
        for (Entity item: items){
            if (item instanceof SwordEntity) {
                characterDamage += 5;
                ((Weapons) item).decrementDurability();
            }
            if(item instanceof Bow){
                ((Weapons) item).decrementDurability();
                entity.receiveDMG(characterDamage);

            }
        }
        if(entity instanceof ZombieToast && ((ZombieToast) entity).HasArmour()) {
            characterDamage = characterDamage/2;
            ((ZombieToast) entity).decrementArmourDurability();
        }
        if(entity instanceof Mercenary && ((Mercenary) entity).HasArmour()) {
            characterDamage = characterDamage/2;
            ((Mercenary) entity).decrementArmourDurability();
        }
        entity.receiveDMG(characterDamage);
        if (!entity.isAlive()){
            character.setInBattle(false);
            for (MovingEntity ally: character.getAllies()){
                ally.setInBattle(false);
            }
            return BattleOutcome.CHARACTER_WINS;
        }
        int enemydamage = (entity.getDamage() + entity.getHealth())/5;
        for (Entity item: items){
            if (item instanceof Shield) {
                enemydamage -= 5;
                ((Weapons) item).decrementDurability();
            }
            if(item instanceof ArmourEntity){
                ((Weapons) item).decrementDurability();
                enemydamage = enemydamage/2;

            }
        }
        character.receiveDMG(enemydamage);
        if(!character.isAlive()) {
            return BattleOutcome.ENEMY_WINS;
        }
        return BattleOutcome.NEITHER;
    }
}