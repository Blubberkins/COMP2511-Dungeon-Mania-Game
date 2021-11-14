package dungeonmania;

import dungeonmania.Battles.BattleOutcome;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Character extends Entity{
    private int health;
    private Damage damage;
    private Boolean inBattle;
    private List<MovingEntity> allies;
    private boolean isInvisible;
    private int invisibleTimer;
    private int invincibleTimer;
    private boolean isInvincible;

    public Character(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        this.damage = new BaseDamage(10, null, null, null);
        this.health = 100;
        this.allies = new ArrayList<>();
        this.inBattle = false;
        this.isInvincible = false;
        this.invincibleTimer = 0;
        this.invisibleTimer = 0;
        this.isInvisible = false;
    }

    /**
     * Updates the characters potential potion effects, and removes them if they
     * have timed out.
     */
    public void updateChar() {
        if (this.getisInvincible()) {
            this.setInvincibleTimer(this.getInvincibleTimer() - 1);
        }
        if (this.getisInvisible()) {
            this.setInvisibleTimer(this.getInvisibleTimer() - 1);
        }
        if (this.getInvincibleTimer() == 0) {
            this.setInvincible(false);
        }
        if (this.getInvisibleTimer() == 0) {
            this.setInvisible(false);
        }
    }
        /**
     * Checks if a position is adjacent
     * 
     * @param e
     * @return boolean
     */
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
    public Entity doBattle(Character updateCharacter, MovingEntity entity, DungeonMania currentGame, List<Entity> toRemove) {
        Entity tobeRemoved = null;
        BattleOutcome outcome = Battles.Battle(updateCharacter, (MovingEntity) entity,
                            currentGame.getItems());
                    if (outcome == BattleOutcome.CHARACTER_WINS) {
                        tobeRemoved = entity;
                        ((MovingEntity) entity).setInBattle(false);
                        this.setInBattle(false);
                        if (entity instanceof ZombieToast && ((ZombieToast) entity).HasArmour()) {
                            currentGame.winItem(((ZombieToast) entity).getArmour());
                        }
                        if (entity instanceof Mercenary && ((Mercenary) entity).HasArmour()) {
                            currentGame.winItem(((Mercenary) entity).getArmour());
                        }
                        int probability = ThreadLocalRandom.current().nextInt(0, 11);
                        if (probability == 1) {
                            currentGame.AddItem("one_ring");
                        }

                    } else if (outcome == BattleOutcome.ENEMY_WINS) {
                        ((MovingEntity) entity).setInBattle(false);
                        Boolean HasOneRing = false;
                        for (Entity item : currentGame.getItems()) {
                            if (item instanceof TheOneRingEntity) {
                                HasOneRing = true;
                                ((TheOneRingEntity) item).setIsUsed(true);
                            }
                        }
                        if (!HasOneRing) {
                            tobeRemoved = updateCharacter;
                        } else {
                            this.setHealth(30);
                            this.setInBattle(false);
                            
                        }

                    }
                    currentGame.removeUsedItems();
                    return tobeRemoved;
    }

    public boolean RealisBomb(Position e) {
        if(RealisAdjacent(e)) {
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

    public DungeonResponse processItem(String itemUsed, DungeonMania currentGame, List<String> buildables) throws InvalidActionException{ 
        if (itemUsed != null) {
            if (currentGame.getItemFromId(itemUsed) == null) {
                throw new InvalidActionException("Item Not In Inventory");
            }
            if (currentGame.getItemFromId(itemUsed).getType().equals("bomb")) {
                    Boolean isActivated = false;
                for (Entity entity : currentGame.getEntities()) {
                    if (entity.getType().equals("switch") && RealisAdjacent(entity.getPos())) {
                        isActivated = ((FloorSwitch) entity).isTriggered();
                    }
                }
                if (!isActivated) {
                    throw new InvalidActionException("not activated");
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
                currentGame.removeItem(currentGame.getItemFromId(itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                        currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            } else if (currentGame.getItemFromId(itemUsed).getType().equals("health_potion")) {
                this.setHealth(30);
                currentGame.removeItem(currentGame.getItemFromId(itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                        currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            } else if (currentGame.getItemFromId(itemUsed).getType().equals("invisibility_potion")) {
                this.setInvisibleTimer(5);
                this.setInvisible(true);
                currentGame.removeItem(currentGame.getItemFromId(itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                        currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            } else if (currentGame.getItemFromId(itemUsed).getType().equals("invincibility_potion")) {
                this.setInvincibleTimer(5);
                if (!currentGame.getDifficulty().equalsIgnoreCase("Hard")) {
                    this.setInvincible(true);
                }
                currentGame.removeItem(currentGame.getItemFromId(itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                        currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            } else {
                throw new IllegalArgumentException("invalid item id");
            }
        }
        return null;
    }
    /**
     * Moves the character in the game given a direction also checks for blocked
     * movement by walls and boulders
     * 
     * @param game
     * @param move
     */
    public void move(DungeonMania game, List<Entity> items, Direction move) {
        Position newPos = this.getPos().translateBy(move);
        for (Entity entity : game.getEntities()) {
            if (entity.getPos().equals(newPos)) {
                if (entity instanceof Boulder) {
                    if (((Boulder) entity).checkBoulderMovable(game.getEntities(), move)) {
                        entity.setPos(entity.getPos().translateBy(move));
                        this.setPos(newPos);
                        return;
                    }
                }
                if (entity instanceof DoorEntity) {
                    for (Entity item : items) {
                        if (item.getType().equals("key")) {
                            ((DoorEntity) entity).setIsOpen(true);
                            ((KeyEntity) item).setIsUsed(true);

                        }
                    }
                }
                if (entity instanceof Portal) {
                    for (Entity otherPortal : game.getEntities()) {
                        if (otherPortal instanceof Portal) {
                            if (!otherPortal.getPos().equals(((Portal) entity).getPos())) {
                                if (((Portal) otherPortal).getColour().equals(((Portal) entity).getColour())) {
                                    this.setPos(otherPortal.getPos());
                                    return;
                                }
                            }
                        }
                    }
                }
                if (entity instanceof Wall) {
                    return;
                }

            }
        }
        this.setPos(newPos);
    }

    public List<MovingEntity> getAllies() {
        return allies;
    }

    public void setAllies(List<MovingEntity> allies) {
        this.allies = allies;
    }

    public void addAlly(MovingEntity entity) {
        this.allies.add(entity);
    }

    public void removeAlly(MovingEntity entity) {
        this.allies.remove(entity);
    }

    public Boolean getInBattle() {
        return inBattle;
    }

    public void setInBattle(Boolean inBattle) {
        this.inBattle = inBattle;
    }

    public int getHealth() {
        return health;
    }

    public boolean getisInvisible() {
        return isInvisible;
    }

    public void setInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }

    public boolean getisInvincible() {
        return isInvincible;
    }

    public int getInvisibleTimer() {
        return invisibleTimer;
    }

    public void setInvisibleTimer(int invisibleTimer) {
        this.invisibleTimer = invisibleTimer;
    }

    public int getInvincibleTimer() {
        return invincibleTimer;
    }

    public void setInvincibleTimer(int invincibleTimer) {
        this.invincibleTimer = invincibleTimer;
    }

    public void setInvincible(boolean isInvincible) {
        this.isInvincible = isInvincible;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Damage getDamage() {
        return this.damage;
    }

    public void setDamage(int damage) {
        this.damage.setDamage(damage);
    }

    public void setWeapon(Damage weapon) {
        this.damage = weapon;
    }

    public void addWeapon(String type) {
        Damage weapon = this.damage;

        // these are multipliers, so we need to unwrap
        if (this.damage instanceof Bow) {
            weapon = ((Bow) this.damage).getWeapon();
        }

        Position pos = weapon.getPos();
        String wtype = weapon.getType();
        String id = weapon.getId();

        if (type.equalsIgnoreCase("bow")) {
            weapon = new Bow(weapon, pos, wtype, id);
        } else if (type.equalsIgnoreCase("sword")) {
            weapon = new SwordEntity(weapon, pos, wtype, id);
        } else if (type.equalsIgnoreCase("anduril")) {
            weapon = new Anduril(weapon, pos, wtype, id);
        } else if (type.equalsIgnoreCase("shield")) {
            weapon = new Shield(weapon, pos, wtype, id);
        }

        // rewrapping
        if (this.damage instanceof Bow) {
            weapon = new Bow(weapon, pos, wtype, id);
        }
    }

    public void receiveDMG(int damage2) {
        this.setHealth(health - damage2);
    }

    public Boolean isAlive() {
        return this.health > 0;
    }

}