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

public class Character extends MovingEntity {
    private int health;
    private int attack;
    private List<CollectableEntities> collectables;
    private List<BuildableEntity> buildables;
    public Character (Position pos, String type, String id){
        super(pos, type, id);
        super.setIsInteractable(false);
    }


}