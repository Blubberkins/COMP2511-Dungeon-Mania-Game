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
    }

    public boolean RealisAdjacent(Position e) {
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        for (Direction d : directions) {
            if (this.getPos().translateBy(d).equals(e)) {
                return true;
            }
        }
        return false;
    }

    public boolean RealisBomb(Position e) {
        if (RealisAdjacent(e)) {
            return true;
        }
        if (this.getPos().translateBy(Direction.UP).translateBy(Direction.LEFT).equals(e)) {
            return true;
        }
        if (this.getPos().translateBy(Direction.UP).translateBy(Direction.RIGHT).equals(e)) {
            return true;
        }
        if (this.getPos().translateBy(Direction.DOWN).translateBy(Direction.LEFT).equals(e)) {
            return true;
        }
        if (this.getPos().translateBy(Direction.DOWN).translateBy(Direction.RIGHT).equals(e)) {
            return true;
        }
        return false;
    }

    public void AddItem(String Type, DungeonMania d) {
        String id = Integer.toString(d.incrementIntId());
        Entity entity = null;
        if (Type.equalsIgnoreCase("wood")) {
            entity = new WoodEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("time_turner")){
            entity = new TimeTurner(null, Type, id);
        }
        if (Type.equalsIgnoreCase("arrow")) {
            entity = new ArrowsEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("key")) {
            entity = new KeyEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("treasure")) {
            entity = new TreasureEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("bomb")) {
            entity = new BombEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("sword")) {
            entity = new SwordEntity(null, null, Type, id);
            this.addWeapon("sword");
        }
        if (Type.equalsIgnoreCase("armour")) {
            entity = new ArmourEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("health_potion")) {
            entity = new HealthPotionEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("invincibility_potion")) {
            entity = new InvincibilityPotionEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("invisibility_potion")) {
            entity = new InvisibilityPotionEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("one_ring")) {
            entity = TheOneRingEntity.getInstance(null, Type, id);
        }
        if (Type.equalsIgnoreCase("sun_stone")) {
            entity = new SunStone(null, Type, id);
        }
        if (Type.equalsIgnoreCase("anduril")) {
            entity = new Anduril(null, null, Type, id);
            this.addWeapon("anduril");
        }
        if (entity != null) {
            this.items.add(entity);
        }
    }   
    
    public DungeonResponse processItem(String itemUsed, DungeonMania currentGame, List<String> buildables){
        if (itemUsed != null) {
            if (currentGame.getItemFromId(itemUsed) == null) {
                return null;
            }
            if (currentGame.getItemFromId(itemUsed).getType().equals("bomb")) {
                    Boolean isActivated = false;
                for (Entity entity : currentGame.getEntities()) {
                    if (entity.getType().equals("switch") && RealisAdjacent(entity.getPos())) {
                        isActivated = ((FloorSwitch) entity).isTriggered();
                    }
                }
                if (!isActivated) {
                    return null;
                }
                List<Entity> removable = new ArrayList<>();
                for (Entity entity : currentGame.getEntities()) {
                    if (RealisBomb(entity.getPos())) {
                        removable.add(entity);
                    }
                }
                for (Entity e : removable) {
                    currentGame.removeEntity(e);
                }
                this.items.remove(currentGame.getItemFromId(this.items, itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                        currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            } else if (currentGame.getItemFromId(itemUsed).getType().equals("health_potion")) {
                this.setHealth(30);
                this.items.remove(currentGame.getItemFromId(this.items, itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                        currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            } else if (currentGame.getItemFromId(itemUsed).getType().equals("invisibility_potion")) {
                this.setInvisibleTimer(5);
                this.setInvisible(true);
                this.items.remove(currentGame.getItemFromId(this.items, itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                        currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            } else if (currentGame.getItemFromId(itemUsed).getType().equals("invincibility_potion")) {
                this.setInvincibleTimer(5);
                if (!currentGame.getDifficulty().equalsIgnoreCase("Hard")) {
                    this.setInvincible(true);
                }
                this.items.remove(currentGame.getItemFromId(this.items, itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                        currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            } else {
                throw new IllegalArgumentException("invalid item id");
            }
        }
        return null;
    }
    public Entity OlderPlayerBattle(DungeonMania dungeonmania, Character character) {
        Entity tobeRemoved = null;
        BattleOutcome outcome = Battles.BattleYourself(character,this,
                            dungeonmania.getItems(), this.items);
        if (outcome == BattleOutcome.CHARACTER_WINS) {
            tobeRemoved = this;
        }
        if (outcome == BattleOutcome.ENEMY_WINS) {
            tobeRemoved = character;
        }
        return tobeRemoved;
    }


}
