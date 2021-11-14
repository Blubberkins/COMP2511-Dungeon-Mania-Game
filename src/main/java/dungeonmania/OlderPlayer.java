package dungeonmania;

import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.Chromaticity;

import dungeonmania.Battles.BattleOutcome;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class OlderPlayer extends Character {
    List<Entity> items;

    public List<Entity> getItems() {
        return items;
    }

    public void setItems(List<Entity> items) {
        this.items = items;
    }

    public OlderPlayer(Position pos, String type, String id) {
        super(pos, type, id);
        // TODO Auto-generated constructor stub
    }

    public Entity OlderPlayerBattle(DungeonMania dungeonmania, Character character) {
        Entity tobeRemoved = null;
        BattleOutcome outcome = Battles.BattleYourself(character, this, dungeonmania.getItems(), this.items);
        if (outcome == BattleOutcome.CHARACTER_WINS) {
            tobeRemoved = this;
        }
        if (outcome == BattleOutcome.ENEMY_WINS) {
            tobeRemoved = character;
        }

        return tobeRemoved;
    }

}
