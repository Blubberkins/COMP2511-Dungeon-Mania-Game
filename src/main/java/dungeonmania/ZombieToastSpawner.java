package dungeonmania;

import dungeonmania.util.Position;

public class ZombieToastSpawner extends Wall {
    private int ticksSinceSpawn;
    
    public ZombieToastSpawner(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(true);
        this.ticksSinceSpawn = 0;
    }

    public int getTicksSinceSpawn() {
        return ticksSinceSpawn;
    }

    public void setTicksSinceSpawn(int ticksSinceSpawn) {
        this.ticksSinceSpawn = ticksSinceSpawn;
    }
 
}
