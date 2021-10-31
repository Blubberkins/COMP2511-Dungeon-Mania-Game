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

public class Battles {
    public static enum BattleOutcome {
        CHARACTER_WINS, ENEMY_WINS, NEITHER
    }
    public static BattleOutcome Battle(Character character, MovingEntity entity){
        int totalDamage = character.getDamage();
        entity.receiveDMG(totalDamage);
        if (!entity.isAlive()){
            character.setInBattle(false);
            return BattleOutcome.CHARACTER_WINS;
        }
        character.receiveDMG(entity.getDamage());
        if(!character.isAlive()) {
            return BattleOutcome.ENEMY_WINS;
        }
        return BattleOutcome.NEITHER;
    }
}