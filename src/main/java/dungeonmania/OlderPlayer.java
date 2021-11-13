package dungeonmania;
import java.util.List;

import dungeonmania.Battles.BattleOutcome;
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
        //TODO Auto-generated constructor stub
    }
    public BattleOutcome OlderPlayerBattle(DungeonMania dungeonmania){
        return null;
    }


}
