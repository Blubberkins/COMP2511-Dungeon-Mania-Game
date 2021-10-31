package dungeonmania;

import dungeonmania.util.Position;

public abstract class MovingEntity extends Entity {
    private int health;
    private int damage;
    private Boolean InBattle;   
    public MovingEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        this.InBattle = false;
    }

    /**
     * Checks if a Mercenary is bribed,
     * else is hostile
     * @return boolean
     */
    public Boolean isHostile(){
        if(this instanceof Mercenary && ((Mercenary) this).getIsBribed()){
            return false;
        }
        return true;
    }
    
    public int getHealth() {
        return health;
    }

    public Boolean getInBattle() {
        return InBattle;
    }

    public void setInBattle(Boolean inBattle) {
        InBattle = inBattle;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
    public int getDamage() {
        return this.damage;
    }

    public abstract void move(DungeonMania d);
    public abstract void receiveDMG(int damage);
    
    public Boolean isAlive(){
        return this.health > 0;
    }


}