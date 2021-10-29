package dungeonmania;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;



import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;
import dungeonmania.*;

public class DungeonManiaController {
    private ArrayList<DungeonMania> games;
    private DungeonMania loadedgame;
    
    public DungeonManiaController() {
        this.games = new ArrayList<>();    }
    
    public ArrayList<DungeonMania> getGames() {
        return games;
    }

    public void setGames(ArrayList<DungeonMania> games) {
        this.games = games;
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
        List<ItemResponse> items = new ArrayList<>();
        List<String> buildables = new ArrayList<>();
        DungeonMania dungeonMania = new DungeonMania(gameMode, dungeonName);
        JSONObject dungeon = null;
        File f = new File("src/test/resources/dungeons/" + dungeonName + ".json");
        String s = f.getAbsolutePath();
        
        try {
            FileReader g = new FileReader(s);
            dungeon =  new JSONObject(new JSONTokener(new FileReader(s)));
        } catch (Exception e) {
        }
        JSONArray entities = dungeon.getJSONArray("entities");
        for (int i = 0; i < entities.length(); i++) {
            String type = entities.getJSONObject(i).getString("type");
            int x = entities.getJSONObject(i).getInt("x");
            int y = entities.getJSONObject(i).getInt("y");
            Position pos = new Position(x, y, 0); //placeholder for layer
            dungeonMania.createEntity(pos, type);
        }
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(0,5); i++) {
            dungeonMania.spawnSpider();
        }
        JSONObject jsonGoalCondition = dungeon.getJSONObject("goal-condition");
        dungeonMania.setGoal(GoalFactory.generate(jsonGoalCondition.toString()));
        List<EntityResponse> entityResponses = dungeonMania.getEntityResponses();
        dungeonMania.setId(Integer.toString(this.games.size() + 1));
        this.games.add(dungeonMania);
        this.loadedgame = dungeonMania;
        return new DungeonResponse(dungeonMania.getId(),dungeonName, entityResponses, items, buildables,GoalFactory.goalString(dungeonMania.getGoal()));
    }
        

    
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        return null;
    }

    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        return null;
    }

    public List<String> allGames() {
        return null;
    }

    public DungeonResponse tick(String itemUsed, Direction movementDirection) throws IllegalArgumentException, InvalidActionException {
        String Goalstring;
        DungeonMania currentGame = this.loadedgame;
        List<String> buildables = new ArrayList<>();
        Character updateCharacter = currentGame.getCharacter();
        updateCharacter.move(currentGame, movementDirection);
        currentGame.setCharacter(updateCharacter);
        currentGame.updateEntities(updateCharacter);
        Goal goal = currentGame.getGoal();
        List<Entity> toRemove = new ArrayList<>();
        for (Entity entity: currentGame.getEntities()) {
            if(entity instanceof MovingEntity) {
              ((MovingEntity) entity).move(currentGame);
            }

            if (entity instanceof CollectableEntities) {
                if(updateCharacter.getPos().equals(entity.getPos())){
                    toRemove.add(entity);
                    currentGame.addItem(entity.getId());
                }
            }
        }
        for (Entity entity: toRemove) {
            currentGame.removeEntity(entity);
        }
        
        if (goal.isComplete(currentGame)) {
           Goalstring = "";
        }
        else {
            Goalstring = GoalFactory.goalString(currentGame.getGoal());
        }
        return new DungeonResponse(currentGame.getId(),currentGame.getName(), currentGame.getEntityResponses(), currentGame.getItemResponses(), buildables,Goalstring);

    }

    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        return null;
    }

    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        return null;
    }
}