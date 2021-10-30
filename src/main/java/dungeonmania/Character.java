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
                    for (Item item: game.getItems()) {
                        if (item.getType().equals("key")) {
                            ((DoorEntity) entity).setIsOpen(true);
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


}