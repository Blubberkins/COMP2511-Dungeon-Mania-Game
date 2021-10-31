package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZombieToastSpawner extends StaticEntity {
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
