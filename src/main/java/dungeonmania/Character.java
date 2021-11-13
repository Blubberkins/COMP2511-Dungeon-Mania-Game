package dungeonmania;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;

public class Character extends Entity {
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
        this.health = 30;
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
     * Moves the character in the game given a direction also checks for blocked
     * movement by walls and boulders
     * 
     * @param game
     * @param move
     */
    public void move(DungeonMania game, Direction move) {
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
                    for (Entity item : game.getItems()) {
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