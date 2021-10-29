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

public class BuildableEntity extends CollectableEntities {
    public BuildableEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        //TODO Auto-generated constructor stub
    }
    private Position pos;
    private String type;
}