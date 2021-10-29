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

public class Character extends Entity {
    private int health;
    private int attack;
    private List<CollectableEntities> collectables;
    private List<BuildableEntity> buildables;
    public Character (Position pos, String type, String id){
        super(pos, type, id);
        super.setIsInteractable(false);
    }
    public void move(DungeonMania game, Direction move) {
        Position newPos =  this.getPos().translateBy(move);
        for(Entity entity: game.getEntities()){
            if(entity.getPos().equals(newPos)){
                if(entity instanceof Wall){
                    return;
                }
            }
        }
        this.setPos(newPos);
    }


}