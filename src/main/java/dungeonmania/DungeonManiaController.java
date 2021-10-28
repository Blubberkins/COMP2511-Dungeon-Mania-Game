package dungeonmania;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

public class DungeonManiaController {
    public DungeonManiaController() {
    }

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    public List<String> getGameModes() {
        return Arrays.asList("Standard", "Peaceful", "Hard");
    }

    /**
     * /dungeons
     * 
     * Done for you.
     */
    public static List<String> dungeons() {
        try {
            return FileLoader.listFileNamesInResourceDirectory("/dungeons");
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    /**
     * Initializes game state + spawns in all entities and main character + items
     * @param dungeonName
     * @param gameMode
     * @return DungeonResponse
     * @throws IllegalArgumentException
     */
    public DungeonResponse newGame(String dungeonName, String gameMode) throws IllegalArgumentException {
        DungeonMania dungeonMania = new DungeonMania(gameMode);
        JSONObject dungeon = null;
        try {
            String filename = "src\\main\\resources\\dungeons\\" + dungeonName + ".json";
            dungeon =  new JSONObject(new JSONTokener(new FileReader(filename)));
        } catch (Exception e) {
            
        }
         int width = dungeon.getInt("width");
         int height = dungeon.getInt("height");
         JSONArray entities = dungeon.getJSONArray("entities");
         for (int i = 0; i < entities.length(); i++) {
            String type = entities.getJSONObject(i).getString("type");
            int x = entities.getJSONObject(i).getInt("x");
            int y = entities.getJSONObject(i).getInt("y");
            DungeonMania.createEntity(x,y,type);

        JSONObject jsonGoalCondition = dungeon.getJSONObject("goal-condition");
        GoalCondition condition = recurseGoalCondition(jsonGoalCondition);
        dungeonMania.setCondition = condition;



            

        }

        private GoalCondition recurseGoalCondition(JSONObject currentCondition) {
            String goalType = currentCondition.getString("goal");
    
            GoalCondition nodeCondition;
            switch (goalType) {
                case "enemies":
                default:
                {
                    nodeCondition = new EnemiesCondition();
                    break;
                }
                case "treasure":
                {
                    nodeCondition = new TreasureCondition();
                    break;
                }
                case "exit":
                {
                    nodeCondition = new ExitCondition();
                    break;
                }
                case "boulders":
                {
                    nodeCondition = new BouldersCondition();
                    break;
                }
                case "AND":
                {
                    JSONArray jsonSubGoals = currentCondition.getJSONArray("subgoals");
    
                    ANDCondition andCondition = new ANDCondition();
    
                    for (Object goal : jsonSubGoals)
                    {
                        GoalCondition leaf = recurseGoalCondition((JSONObject)goal);
                        andCondition.addCondition(leaf);
                    }
    
                    nodeCondition = andCondition;
                }
                case "OR":
                {
                    JSONArray jsonSubGoals = currentCondition.getJSONArray("subgoals");
    
                    ORCondition orCondition = new ORCondition();
    
                    for (Object goal : jsonSubGoals)
                    {
                        GoalCondition leaf = recurseGoalCondition((JSONObject)goal);
                        orCondition.addCondition(leaf);
                    }
    
                    nodeCondition = orCondition;
                }
            }
    
            return nodeCondition;
        }
    
        

        return null;
        //visually seeing one of the maps
        //create our dungeon mania object
        // evaluate string gamemode --> State files for each of the game-modes
        //spawning in static entities (abstract entities class)
        //after spawn finish --> player movement
        // spawning in walls first
        //player check for goal completion (recycle the lab) --> we only do basic composite goals (and/or)

        //Sprint 1
        //newgame --> tick
        //Gamemodes
        //Loads in walls
        //player can move
        //player can successfully reach the exit

        //ORDER OF PRIORITY
        //Spawning
        //Create a randomized location for spider,
        //Movement
        //inside abstract entities, update movement abstract function, implemented down
        //Item interactions 
        //Battle

        //Sprint 2
        //loads in one enemy(of choice)
        //movement for enemy
        //battle with said enemy
        //other goals (treasure) and basic composite goals

        //Sprint 3
        //everything else


    }
    
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        return null;
    }

    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        return null;
    }

    public List<String> allGames() {
        return new ArrayList<>();
    }

    public DungeonResponse tick(String itemUsed, Direction movementDirection) throws IllegalArgumentException, InvalidActionException {
        return null;
    }

    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        return null;
    }

    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        return null;
    }
}