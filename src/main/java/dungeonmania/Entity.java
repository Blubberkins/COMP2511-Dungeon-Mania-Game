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

public abstract class Entity {
    private Position pos;
    private String id;
    private Boolean isInteractable;
    private String type;
    public Entity(Position pos, String type, String id){
        this.pos = pos;
        this.id = id;
        this.type = type;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Position getPos() {
        return pos;
    }
    public void setPos(Position pos) {
        this.pos = pos;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Boolean getIsInteractable() {
        return isInteractable;
    }
    public void setIsInteractable(Boolean isInteractable) {
        this.isInteractable = isInteractable;
    }
    
}