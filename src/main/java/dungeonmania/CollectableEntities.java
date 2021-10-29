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

public class CollectableEntities extends Entity {
    public CollectableEntities(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(true);
        //TODO Auto-generated constructor stub
    }
   
}