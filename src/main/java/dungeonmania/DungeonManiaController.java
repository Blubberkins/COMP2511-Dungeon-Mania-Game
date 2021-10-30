package dungeonmania;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        this.games = new ArrayList<>();
    }

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
     * 
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
        File f = new File("src/main/resources/dungeons/" + dungeonName + ".json");
        String s = f.getAbsolutePath();

        try {
            FileReader g = new FileReader(s);
            dungeon = new JSONObject(new JSONTokener(new FileReader(s)));
        } catch (Exception e) {
        }
        JSONArray entities = dungeon.getJSONArray("entities");
        for (int i = 0; i < entities.length(); i++) {
            String type = entities.getJSONObject(i).getString("type");
            int x = entities.getJSONObject(i).getInt("x");
            int y = entities.getJSONObject(i).getInt("y");
            Position pos = new Position(x, y, 0); //placeholder for layer
            if (type.equalsIgnoreCase("portal")) {
                dungeonMania.createPortal(pos, type, entities.getJSONObject(i).getString("colour"));
            }
            else {
                dungeonMania.createEntity(pos, type);
            }
        }
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(0, 5); i++) {
            dungeonMania.spawnSpider();
        }
        JSONObject jsonGoalCondition = dungeon.getJSONObject("goal-condition");
        dungeonMania.setGoal(GoalFactory.generate(jsonGoalCondition.toString()));
        List<EntityResponse> entityResponses = dungeonMania.getEntityResponses();
        dungeonMania.setId(Integer.toString(this.games.size() + 1));
        this.games.add(dungeonMania);
        this.loadedgame = dungeonMania;
        return new DungeonResponse(dungeonMania.getId(), dungeonName, entityResponses, items, buildables,
                GoalFactory.goalString(dungeonMania.getGoal()));
    }

    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        int numSaves = allGames().size();
        String difficulty = this.loadedgame.getDifficulty();
        String mapName = this.loadedgame.getName();
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("difficulty", difficulty);
        jsonObject.put("mapName", mapName);

        JSONArray entities = new JSONArray();
        List<Entity> gameEntities = this.loadedgame.getEntities();

        for (Entity entity : gameEntities) {
            JSONObject newEntity = new JSONObject();
            int xPos = entity.getPos().getX();
            int yPos = entity.getPos().getY();
            String type = entity.getType();
            newEntity.put("x", xPos);
            newEntity.put("y", yPos);
            newEntity.put("type", type);
            entities.put(newEntity);
        }

        jsonObject.put("entities", entities);

        Goal gameGoals = this.loadedgame.getGoal();
        JSONObject goals = goalToJSON(gameGoals);
        jsonObject.put("goal-condition", goals);

        String conts = jsonObject.toString();

        try {
            String pathname = "src/main/resources/saves/";
            String filename = difficulty + "-" + mapName + "-" + Integer.toString(numSaves) + "-" + ".json";
            File f = new File(pathname);
            String absolutePathName = f.getAbsolutePath();
            File save = new File(absolutePathName + filename);
            save.createNewFile();
            writeToFile(conts, absolutePathName + "/" + filename);
        } catch (IOException e) {

        }

        return new DungeonResponse(this.loadedgame.getId(), this.loadedgame.getName(), this.loadedgame.getEntityResponses(), this.loadedgame.getItemResponses(), null,
                GoalFactory.goalString(this.loadedgame.getGoal()));
    }

    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        try {
            List<String> saves = allGames();
            if (!saves.contains(name)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {

        }

        List<ItemResponse> items = new ArrayList<>();
        List<String> buildables = new ArrayList<>();

        File f = new File("src/main/resources/saves/" + name);
        String s = f.getAbsolutePath();
        JSONObject dungeon = null;

        try {
            FileReader g = new FileReader(s);
            dungeon = new JSONObject(new JSONTokener(new FileReader(s)));
        } catch (Exception e) {
        }

        String difficulty = dungeon.getString("difficulty");
        String mapName = dungeon.getString("mapName");
        DungeonMania dungeonMania = new DungeonMania(difficulty, mapName);

        JSONArray entities = dungeon.getJSONArray("entities");
        for (int i = 0; i < entities.length(); i++) {
            String type = entities.getJSONObject(i).getString("type");
            int x = entities.getJSONObject(i).getInt("x");
            int y = entities.getJSONObject(i).getInt("y");
            Position pos = new Position(x, y, 0); // placeholder for layer
            dungeonMania.createEntity(pos, type);
        }

        JSONObject jsonGoalCondition = dungeon.getJSONObject("goal-condition");
        dungeonMania.setGoal(GoalFactory.generate(jsonGoalCondition.toString()));
        List<EntityResponse> entityResponses = dungeonMania.getEntityResponses();
        dungeonMania.setId(Integer.toString(this.games.size() + 1));
        this.games.add(dungeonMania);
        this.loadedgame = dungeonMania;
        return new DungeonResponse(dungeonMania.getId(), mapName, entityResponses, items, buildables,
                GoalFactory.goalString(dungeonMania.getGoal()));

    }

    public List<String> allGames() {
        File f = new File("src/main/resources/");
        String filepath = f.getAbsolutePath();
        Boolean madeDirectory = new File(filepath + "/saves/").mkdir();
        List<String> saves = new ArrayList<>();
            File savespath = new File(filepath + "/saves/");
            saves = Arrays.asList(savespath.list());
        return saves;
    }

    public DungeonResponse tick(String itemUsed, Direction movementDirection)
            throws IllegalArgumentException, InvalidActionException {
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
            if (entity instanceof MovingEntity) {
              ((MovingEntity) entity).move(currentGame);
            }

            if (entity instanceof CollectableEntities) {
                if (updateCharacter.getPos().equals(entity.getPos())) {
                    toRemove.add(entity);
                    currentGame.AddItem(entity.getType());
                }
            }
            if (entity instanceof StaticEntity) {
                if (entity instanceof FloorSwitch) {
                    if (((FloorSwitch) entity).checkTriggered(currentGame.getEntities())) {
                        ((FloorSwitch) entity).setisTriggered(true);
                    }
                }
            }
        }
        for (Entity entity : toRemove) {
            currentGame.removeEntity(entity);
        }

        if (goal.isComplete(currentGame)) {
            Goalstring = "";
        } else {
            Goalstring = GoalFactory.goalString(currentGame.getGoal());
        }
        return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                currentGame.getItemResponses(), buildables, Goalstring);

    }

    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        return null;
    }

    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        if(!buildable.equals("bow") && !buildable.equals("shield")){
            throw new IllegalArgumentException();
        }
        int keys = 0;
        int wood = 0;
        int arrows = 0;
        int treasure = 0;
        DungeonMania dungeon = this.loadedgame;
        for (Entity item: dungeon.getItems()){
            if (item.getType().equals("wood")) {
                wood++;
            }
            if (item.getType().equals("key") && ((KeyEntity) item).getIsUsed()) {
                keys++;
            }
            if (item.getType().equals("arrow")) {
                arrows++;
            }
            if(item.getType().equals("treasure")) {
                treasure++;
            }
        }
        if(buildable.equals("Bow")) {
            if(wood >= 1 &&  arrows >= 3) {
                dungeon.addBuildable("Bow");
            }
        }
        return null;
    }

    public void writeToFile(String conts, String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(conts);
            fw.close();
        } catch (IOException e) {

        }
    }

    public JSONObject goalToJSON(Goal goal) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("goal", goal.getName());

        if (goal instanceof GoalComposite) {
            List<Goal> subgoals = ((GoalComposite) goal).getSubgoals();
            JSONArray array = new JSONArray();
            for (Goal subgoal : subgoals) {
                array.put(goalToJSON(subgoal));
            }
            jsonObject.put("subgoals", array);
        }

        return jsonObject;
    }
}