package dungeonmania;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import dungeonmania.Battles.BattleOutcome;

public class DungeonManiaController {
    private ArrayList<DungeonMania> games;
    private DungeonMania loadedgame;

    public DungeonManiaController() {
        this.games = new ArrayList<>();
    }

    /**
     * Gets list of existing games
     * @return ArrayList<DungeonMania>
     */
    public ArrayList<DungeonMania> getGames() {
        return games;
    }

    /**
     * Sets the list of existing games
     * @param games
     */
    public void setGames(ArrayList<DungeonMania> games) {
        this.games = games;
    }

    /**
     * Gets current skin
     * @return String
     */
    public String getSkin() {
        return "default";
    }

    /**
     * Gets localisation
     * @return String
     */
    public String getLocalisation() {
        return "en_US";
    }

    /**
     * Gets list of gamemodes
     * @return List<String>
     */
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
            dungeon = new JSONObject(new JSONTokener(new FileReader(s)));
        } catch (Exception e) {
        }
        JSONArray entities = dungeon.getJSONArray("entities");
        for (int i = 0; i < entities.length(); i++) {
            String type = entities.getJSONObject(i).getString("type");
            int x = entities.getJSONObject(i).getInt("x");
            int y = entities.getJSONObject(i).getInt("y");
            Position pos = new Position(x, y, 0); // placeholder for layer
            if (type.equalsIgnoreCase("portal")) {
                dungeonMania.createPortal(pos, type, entities.getJSONObject(i).getString("colour"));
            } else {
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

    /**
     * Saves a game given the name of the game instance
     * @param name
     * @return DungeonResponse
     * @throws IllegalArgumentException
     */
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

        JSONArray inventory = new JSONArray();
        List<Entity> gameItems = this.loadedgame.getItems();

        for (Entity item : gameItems) {
            JSONObject newEntity = new JSONObject();
            String type = item.getType();
            newEntity.put("type", type);
            inventory.put(newEntity);
        }

        jsonObject.put("entities", entities);
        jsonObject.put("inventory", inventory);

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

        String id = this.loadedgame.getId();
        String gameName = this.loadedgame.getName();
        List<EntityResponse> entityResponses = this.loadedgame.getEntityResponses();
        List<ItemResponse> itemResponses = this.loadedgame.getItemResponses();
        String goalString = GoalFactory.goalString(this.loadedgame.getGoal());
        this.loadedgame = null;

        return new DungeonResponse(id, gameName, entityResponses, itemResponses, null, goalString);
    }

    /**
     * Loads a specific game given the name of the game
     * @param name
     * @return DungeonResponse
     * @throws IllegalArgumentException
     */
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

        JSONArray inventory = dungeon.getJSONArray("inventory");
        for (int i = 0; i < inventory.length(); i++) {
            String type = inventory.getJSONObject(i).getString("type");
            dungeonMania.AddItem(type);
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

    /**
     * Checks if a position is adjacent 
     * @param e
     * @return boolean
     */
    public boolean RealisAdjacent(Position e) {
        DungeonMania dungeon = this.loadedgame;
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        for (Direction d: directions) {
            if(dungeon.getCharacter().getPos().translateBy(d).equals(e)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a mercenary is adjacent to a 
     * given position
     * @param e
     * @return boolean
     */
    public boolean isMercenaryAdjacent(Position e) {
        DungeonMania dungeon = this.loadedgame;
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        for (Direction d: directions) {
            if(dungeon.getCharacter().getPos().translateBy(d).translateBy(d).equals(e)){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of all the games (saved and current)
     * @return List<String>
     */
    public List<String> allGames() {
        File f = new File("src/main/resources/");
        String filepath = f.getAbsolutePath();
        List<String> saves = new ArrayList<>();
        File savespath = new File(filepath + "/saves/");
        saves = Arrays.asList(savespath.list());
        return saves;
    }

    /**
     * Advances the game state by one tick
     * also calculates what craftables the character may have access to
     * uses items if items have been used
     * and moves the character given the direction
     * does battle, and checks if the game has ended through
     * the goal condition/ player death.
     * and updates the state of all entities in the game.
     * @param itemUsed
     * @param movementDirection
     * @return
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse tick(String itemUsed, Direction movementDirection)
            throws IllegalArgumentException, InvalidActionException {
        String Goalstring;
        DungeonMania currentGame = this.loadedgame;
        List<String> buildables = new ArrayList<>();
        Character updateCharacter = currentGame.getCharacter();
        if (!updateCharacter.getInBattle()) {
            updateCharacter.move(currentGame, movementDirection);
            currentGame.setCharacter(updateCharacter);
            currentGame.updateEntities(updateCharacter);
        }
        CheckBow();
        CheckShield();
        Goal goal = currentGame.getGoal();
        List<Entity> toRemove = new ArrayList<>();
        updateCharacter.updateChar();
        if (itemUsed != null) {
            if(currentGame.getItemFromId(itemUsed).getType().equals("bomb")){
                Boolean isActivated = false;
                for (Entity entity:currentGame.getEntities()){
                    if(entity.getType().equals("switch") && RealisAdjacent(entity.getPos())) {
                       isActivated = ((FloorSwitch) entity).isTriggered();
                    }                   
                }
                if(!isActivated) {
                    throw new InvalidActionException("bomb");
                }
                List<Entity> removable = new ArrayList<>();
                for (Entity entity:currentGame.getEntities()) {
                    if(RealisAdjacent(entity.getPos())) {
                        removable.add(entity);
                    }
                }
                for (Entity e: removable) {
                    currentGame.removeEntity(e);
                }
                currentGame.removeItem(currentGame.getItemFromId(itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            }
            if (currentGame.getItemFromId(itemUsed).getType().equals("health_potion")) {
                updateCharacter.setHealth(30);
                currentGame.removeItem(currentGame.getItemFromId(itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            }
            if (currentGame.getItemFromId(itemUsed).getType().equals("invisibility_potion")) {
                updateCharacter.setInvisibleTimer(5);
                updateCharacter.setInvisible(true);
                currentGame.removeItem(currentGame.getItemFromId(itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            }
            if (currentGame.getItemFromId(itemUsed).getType().equals("invincibility_potion")) {
                updateCharacter.setInvincibleTimer(5);
                if(!currentGame.getDifficulty().equalsIgnoreCase("Hard")){
                    updateCharacter.setInvincible(true);
                }
                currentGame.removeItem(currentGame.getItemFromId(itemUsed));
                return new DungeonResponse(currentGame.getId(), currentGame.getName(), currentGame.getEntityResponses(),
                currentGame.getItemResponses(), buildables, GoalFactory.goalString(currentGame.getGoal()));
            }
        }
        List<Entity> zombieToastSpawners = new ArrayList<>();
        for (Entity entity: currentGame.getEntities()) {
            if (entity instanceof MovingEntity) {
                if(updateCharacter.getInBattle() && !((MovingEntity) entity).getInBattle()){
                    ((MovingEntity) entity).move(currentGame);
                }
                if(updateCharacter.getInBattle() && entity instanceof Mercenary && !((MovingEntity) entity).getInBattle()){
                    if(isMercenaryAdjacent(entity.getPos())) {
                    ((MovingEntity) entity).move(currentGame);
                    }
                }

                if (!updateCharacter.getInBattle() && !((MovingEntity) entity).getInBattle()) {
                    ((MovingEntity) entity).move(currentGame);
                    if (((MovingEntity) entity).isHostile() && updateCharacter.getPos().equals(entity.getPos()) && !currentGame.getDifficulty().equalsIgnoreCase("peaceful")) {
                        if(!loadedgame.getCharacter().getisInvisible()) {
                        updateCharacter.setInBattle(true);
                        ((MovingEntity) entity).setInBattle(true);
                        }
                    }
                }
                if(updateCharacter.getInBattle() && ((MovingEntity) entity).getInBattle() && ((MovingEntity) entity).isHostile()){
                    BattleOutcome outcome = Battles.Battle(updateCharacter, (MovingEntity) entity, currentGame.getItems());
                    if( outcome == BattleOutcome.CHARACTER_WINS) {
                        toRemove.add(entity);
                        if(entity instanceof ZombieToast && ((ZombieToast) entity).HasArmour()) {
                            currentGame.winItem(((ZombieToast) entity).getArmour());
                        }
                        if(entity instanceof Mercenary && ((Mercenary) entity).HasArmour()) {
                            currentGame.winItem(((Mercenary) entity).getArmour());
                        }
                        int probability = ThreadLocalRandom.current().nextInt(0, 11);
                        if(probability == 1){
                            currentGame.AddItem("one_ring");
                        }

                    }
                    else if (outcome == BattleOutcome.ENEMY_WINS){
                        Boolean HasOneRing = false;
                        for (Entity item: currentGame.getItems()){
                            if (item instanceof TheOneRingEntity) {
                                HasOneRing = true;
                                ((TheOneRingEntity) item).setIsUsed(true);
                            }
                        }
                        if(!HasOneRing) {
                        toRemove.add(updateCharacter);
                        }
                        else {
                            Character newLife = currentGame.getCharacter();
                            newLife.setHealth(30);
                            currentGame.setCharacter(newLife);
                        }
                        
                    }
                    currentGame.removeUsedItems();
                }
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
                if (entity instanceof ZombieToastSpawner) {
                    ((ZombieToastSpawner) entity).setTicksSinceSpawn(((ZombieToastSpawner) entity).getTicksSinceSpawn() + 1);
                    Boolean Standardspawn = ((ZombieToastSpawner) entity).getTicksSinceSpawn() % 20 == 0;
                    Boolean IsOnHard = currentGame.getDifficulty().equalsIgnoreCase("hard");
                    Boolean HardSpawn = ((ZombieToastSpawner) entity).getTicksSinceSpawn() % 15 == 0;
                    if ((Standardspawn && !IsOnHard) || (HardSpawn && IsOnHard)) {
                        zombieToastSpawners.add(entity);
                        ((ZombieToastSpawner) entity).setTicksSinceSpawn(0);
                    }
                }
            }
        }
        if(zombieToastSpawners.size() > 0) {
            for (Entity zombie: zombieToastSpawners) {
                currentGame.spawnZombie(((ZombieToastSpawner) zombie).getPos());
            }
        }
    

        for (Entity entity : toRemove) {
            currentGame.removeEntity(entity);
        }

        String id = currentGame.getId();
        String name = currentGame.getName();
        List<EntityResponse> e = currentGame.getEntityResponses();
        List<ItemResponse> i = currentGame.getItemResponses();
        if (goal.isComplete(currentGame)) {
            Goalstring = "";
            this.loadedgame = null;
        } else {
            Goalstring = GoalFactory.goalString(currentGame.getGoal());
        }
        return new DungeonResponse(id, name, e, i, currentGame.getBuildables(), Goalstring);

    }

    /**
     * Interacts with a given entity Id
     * @param entityId
     * @return DungeonResponse
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        DungeonMania loadedgame = this.loadedgame;
        Entity interactableEntity = null;
        for (Entity entity: loadedgame.getEntities()){
            if(entity.getId().equals(entityId)) {
                interactableEntity = entity;
            }
        }
        if (!(interactableEntity instanceof Mercenary || interactableEntity instanceof ZombieToastSpawner)) {
            throw new IllegalArgumentException("not a mercenary or Zombie Spawner");
        }
        Entity sword = null;
        Entity treasure = null;
        for (Entity item: loadedgame.getItems()) {
            if(item.getType().equalsIgnoreCase("treasure")) {
            treasure = item; 
            }
            if(item.getType().equalsIgnoreCase("sword")) {
                sword = item; 
                }
        }
        if(interactableEntity instanceof Mercenary && treasure != null) {
            if(!isMercenaryAdjacent(interactableEntity.getPos())){
                throw new InvalidActionException("too far away");
            }
            loadedgame.removeItem(treasure);
            Character updateCharacter = loadedgame.getCharacter();
            updateCharacter.addAlly((Mercenary) interactableEntity);
            loadedgame.setCharacter(updateCharacter);
            ((Mercenary) interactableEntity).setIsBribed(true);
        }
        if (interactableEntity instanceof ZombieToastSpawner) {
            if (!RealisAdjacent(interactableEntity.getPos()) || sword == null) {
                throw new InvalidActionException("zombie spawner error");
            }
            if (RealisAdjacent(interactableEntity.getPos()) && sword != null) {
                loadedgame.removeEntity(interactableEntity);
            }
        } 
        return new DungeonResponse(loadedgame.getId(), loadedgame.getName(),loadedgame.getEntityResponses(), loadedgame.getItemResponses(), null, GoalFactory.goalString(loadedgame.getGoal()));
    }

    /** 
     * Checks if the character can craft a bow
     * @return boolean
    */
    public Boolean CheckBow(){
        int wood = 0;
        int arrows = 0;
        DungeonMania dungeon = this.loadedgame;
        for (Entity item : dungeon.getItems()) {
            if (item.getType().equals("wood")) {
                wood++;
            }
            if (item.getType().equals("arrow")) {
                arrows++;
            }
        }
        if(wood >= 1 && arrows >= 3) {
            if(!dungeon.getBuildables().contains("bow")){
            dungeon.addToBuildableEntities("bow");
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the character can craft a shield
     * @return boolean
     */
    public Boolean CheckShield(){
        int keys = 0;
        int wood = 0;
        int treasure = 0;
        DungeonMania dungeon = this.loadedgame;
        for (Entity item : dungeon.getItems()) {
            if (item.getType().equals("wood")) {
                wood++;
            }
            if (item.getType().equals("key") && ((KeyEntity) item).getIsUsed()) {
                keys++;
            }

            if (item.getType().equals("treasure")) {
                treasure++;
            }
        }
        if((treasure >= 1|| keys >= 1) && wood >= 1) {
            if(!dungeon.getBuildables().contains("Shield")){
            dungeon.addToBuildableEntities("shield");
            }
            return true;
        }
        return false;
    }
    
    /**
     * Builds a bow given the string of the type
     * throws error if the desired craft
     * is not bow or shield
     * @param buildable
     * @return DungeonResponse
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        if (!buildable.equals("bow") && !buildable.equals("shield")) {
            throw new IllegalArgumentException();
        }
        DungeonMania dungeon = this.loadedgame;
        if (buildable.equals("bow")) {
            if(CheckBow()) {
                dungeon.addBuildable("bow");
            }
        }

        if (buildable.equals("shield")) {
            if (CheckShield()) {
                dungeon.addBuildable("shield");
            }
        }
        return new DungeonResponse(loadedgame.getId(), loadedgame.getName(),loadedgame.getEntityResponses(), loadedgame.getItemResponses(), loadedgame.getBuildables(), GoalFactory.goalString(loadedgame.getGoal()));
    }

    /**
     * Writes the given contents 
     * to a given filename
     * @param conts
     * @param filename
     */
    public void writeToFile(String conts, String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(conts);
            fw.close();
        } catch (IOException e) {

        }
    }

    /**
     * Turns a given goal the a json file
     * @param goal
     * @return JSONObject
     */
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

    /**
     * Gets the loaded game
     * @return DungeonMania
     */
    public DungeonMania getLoadedGame() {
        return this.loadedgame;
    }
}