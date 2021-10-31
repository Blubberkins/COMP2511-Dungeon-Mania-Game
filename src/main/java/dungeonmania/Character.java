package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Character extends Entity {
    private int health;
    private int damage;
    private Boolean inBattle;
    private List<MovingEntity> allies;
    private boolean isInvisible;
    private int invisibleTimer;
    private int invincibleTimer;
    private boolean isInvincible;
    public Character (Position pos, String type, String id){
        super(pos, type, id);
        super.setIsInteractable(false);
        this.damage = 10;
        this.health = 30;
        this.allies = new ArrayList<>();
        this.inBattle = false;
        this.isInvincible = false;
        this.invincibleTimer = 0;
        this.invisibleTimer = 0;
        this.isInvisible = false;
    }

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
    
    public List<MovingEntity> getAllies() {
        return allies;
    }

    public void setAllies(List<MovingEntity> allies) {
        this.allies = allies;
    }

    public void addAlly(MovingEntity entity){
        this.allies.add(entity);
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

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void move(DungeonMania game, Direction move) {
        Position newPos =  this.getPos().translateBy(move);
        for (Entity entity: game.getEntities()) {
            if (entity.getPos().equals(newPos)) {
                if (entity instanceof Boulder) {
                    if (((Boulder) entity).checkBoulderMovable(game.getEntities(), move)) { 
                        entity.setPos(entity.getPos().translateBy(move));
                        this.setPos(newPos);
                        return;
                    }
                }
                if (entity instanceof DoorEntity) {
                    for (Entity item: game.getItems()) {
                        if (item.getType().equals("key")) {
                            ((DoorEntity) entity).setIsOpen(true);
                            ((KeyEntity) entity).setIsUsed(true);

                        }
                    }
                }
                if (entity instanceof Portal) {
                    for (Entity otherPortal: game.getEntities()) {
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
                if (entity instanceof Wall){
                    return;
                }

            }
        }
        this.setPos(newPos);
    }

    public void receiveDMG(int damage2) {
        this.setHealth(health - damage2);
    }

    public Boolean isAlive(){
        return this.health > 0;
    }

}