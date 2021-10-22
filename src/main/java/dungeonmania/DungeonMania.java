package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DungeonMania {
    private Character character;
    private Map game;
    private List<MovingEntities> movingEntities;
    private List<StaticEntities> staticEntities;
    private Goal goal;
}
