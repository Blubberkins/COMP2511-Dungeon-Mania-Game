package dungeonmania;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.jetty.util.DateCache.Tick;
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
import dungeonmania.util.Prims;
import dungeonmania.Battles.BattleOutcome;

public class DungeonManiaController {
    private ArrayList<DungeonMania> games;
    private DungeonMania loadedgame;
    private int tick;
    private int TimeTravelDuration;
    private Map<Integer, Direction> oldDirection;
    private Map <Integer, String> oldItem;
    public DungeonManiaController() {
        this.games = new ArrayList<>();
        this.tick = 0;
        this.oldDirection = new HashMap<>();
        this.oldItem = new HashMap<>();
        this.TimeTravelDuration = 0;
    }

    /**
     * Gets list of existing games
     * 
     * @return ArrayList<DungeonMania>
     */
    public ArrayList<DungeonMania> getGames() {
        return games;
    }

    /**
     * Sets the list of existing games
     * 
     * @param games
     */
    public void setGames(ArrayList<DungeonMania> games) {
        this.games = games;
    }

    /**
     * Gets current skin
     * 
     * @return String
     */
    public String getSkin() {
        return "default";
    }

    /**
     * Gets localisation
     * 
     * @return String
     */
    public String getLocalisation() {
        return "en_US";
    }

    /**
     * Gets list of gamemodes
     * 
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
        Boolean peaceful = gameMode.equalsIgnoreCase("peaceful");
        Boolean standard = gameMode.equalsIgnoreCase("standard");
        Boolean hard = gameMode.equalsIgnoreCase("hard");
        if (!peaceful && !standard && !hard) {
            throw new IllegalArgumentException("invalid gamemode");
        }
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException("invalid dungeonName");
        }
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
            } else if (type.equalsIgnoreCase("swamp_tile")) {
                if (entities.getJSONObject(i).has("movement_factor")) {
                    int movement_factor = entities.getJSONObject(i).getInt("movement_factor");
                    dungeonMania.addSwampTile(new SwampTile(new Position(x, y), movement_factor));
                } else {
                    dungeonMania.addSwampTile(new SwampTile(new Position(x, y)));
                }
            } else {
                dungeonMania.createEntity(pos, type);
            }
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
        return new DungeonResponse(dungeonMania.getId(), dungeonName, entityResponses, items, buildables,
                GoalFactory.goalString(dungeonMania.getGoal()));
    }

    /**
     * Saves a game given the name of the game instance
     * 
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
            File save = new File(absolutePathName + "/" + filename);
            save.createNewFile();
            writeToFile(conts, absolutePathName + "/" + filename);
        } catch (IOException e) {

        }

        String id = this.loadedgame.getId();
        String gameName = this.loadedgame.getName();
        List<EntityResponse> entityResponses = this.loadedgame.getEntityResponses();
        List<ItemResponse> itemResponses = this.loadedgame.getItemResponses();
        String goalString = GoalFactory.goalString(this.loadedgame.getGoal());

        return new DungeonResponse(id, gameName, entityResponses, itemResponses, null, goalString);
    }

    /**
     * Loads a specific game given the name of the game
     * 
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
     * 
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
        for (Direction d : directions) {
            if (dungeon.getCharacter().getPos().translateBy(d).equals(e)) {
                return true;
            }
        }
        return false;
    }

    public boolean RealisBomb(Position e) {
        DungeonMania dungeon = this.loadedgame;
        if(RealisAdjacent(e)) {
            return true;
        }
        if (dungeon.getCharacter().getPos().translateBy(Direction.UP).translateBy(Direction.LEFT).equals(e)) {
            return true;
        }
        if (dungeon.getCharacter().getPos().translateBy(Direction.UP).translateBy(Direction.RIGHT).equals(e)) {
            return true;
        }
        if (dungeon.getCharacter().getPos().translateBy(Direction.DOWN).translateBy(Direction.LEFT).equals(e)) {
            return true;
        }
        if (dungeon.getCharacter().getPos().translateBy(Direction.DOWN).translateBy(Direction.RIGHT).equals(e)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a mercenary is adjacent to a given position
     * 
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
        for (Direction d : directions) {
            if (dungeon.getCharacter().getPos().translateBy(d).translateBy(d).equals(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of all the games (saved and current)
     * 
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
     * Advances the game state by one tick also calculates what craftables the
     * character may have access to uses items if items have been used and moves the
     * character given the direction does battle, and checks if the game has ended
     * through the goal condition/ player death. and updates the state of all
     * entities in the game.
     * 
     * @param itemUsed
     * @param movementDirection
     * @return
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse tick(String itemUsed, Direction movementDirection)
            throws IllegalArgumentException, InvalidActionException {
        String Goalstring;
        Boolean isTimeTravelling = false;
        oldDirection.put(this.tick, movementDirection);
        oldItem.put(this.tick, itemUsed);
        DungeonMania currentGame = this.loadedgame;
        List<String> buildables = new ArrayList<>();
        Entity RemovablePlayer = null;
        List<Entity> removables = new ArrayList<>();
        Character updateCharacter = currentGame.getCharacter();
        for (Entity olderPlayer: currentGame.getEntities()) {
            if (olderPlayer instanceof OlderPlayer){
                if(TimeTravelDuration == 0) {
                   RemovablePlayer = olderPlayer;
                }
                else {
                    if(TimeTravelDuration > 0) {
                        TimeTravelDuration--;
                    }
                    ((OlderPlayer) olderPlayer).move(currentGame, ((OlderPlayer) olderPlayer).getItems(), this.oldDirection.get(tick - TimeTravelDuration));
                    if(itemUsed != null && oldItem.get(this.tick - TimeTravelDuration) != null) {
                        DungeonResponse d = ((OlderPlayer) olderPlayer).processItem(oldItem.get(this.tick - TimeTravelDuration), currentGame, currentGame.getBuildables());
                        if(d != null) {
                            return d;
                        }
                    }
                    for (Entity firstentity: currentGame.getEntities()) {
                        Entity toRemove = null;
                        if (firstentity instanceof MovingEntity && firstentity.getPos().equals(olderPlayer.getPos())) {
                            Entity e = ((OlderPlayer) olderPlayer).doBattle((OlderPlayer) olderPlayer, (MovingEntity) firstentity, currentGame, removables);
                            if( e != null) {
                                removables.add(e);
                            }
                        }
                        if(firstentity instanceof Character && !(firstentity instanceof OlderPlayer) && firstentity.getPos().equals(olderPlayer.getPos())){
                            while(toRemove == null) {
                                toRemove = ((OlderPlayer) olderPlayer).OlderPlayerBattle(currentGame, (Character) firstentity);
                                if(toRemove != null) {
                                    removables.add(toRemove);
                                }
                            }
                        }
                    }
                }
            }
        }
        if(itemUsed != null) {
            DungeonResponse d = updateCharacter.processItem(itemUsed, currentGame, currentGame.getBuildables());
            if(d != null) {
                return d;
            }
        }
        if(RemovablePlayer != null) {
            currentGame.removeEntity(RemovablePlayer);
        }
        for (Entity entity: removables) {
            currentGame.removeEntity(entity);
        }
        if (!updateCharacter.getInBattle()) {
            updateCharacter.move(currentGame,currentGame.getItems(), movementDirection);
            currentGame.setCharacter(updateCharacter);
            currentGame.updateEntities(updateCharacter);
        }
        CheckBow();
        CheckShield();
        Goal goal = currentGame.getGoal();
        List<Entity> toRemove = new ArrayList<>();
        updateCharacter.updateChar();
        if (tick % 30 == 0 && tick != 0) {
            currentGame.spawnMercenary();
        }
        if (tick % 50 == 0 && tick != 0 && currentGame.getDifficulty().equalsIgnoreCase("hard")) {
            currentGame.spawnHydra();
        }
 
        
        List<Entity> zombieToastSpawners = new ArrayList<>();
        for (Entity entity : currentGame.getEntities()) {
            if (entity instanceof MovingEntity) {
                if (entity instanceof Mercenary) {
                    int ticksLeftOnBribe = ((Mercenary) entity).getTicksLeftOnBribe();
                    if (ticksLeftOnBribe > 1) {
                        ((Mercenary) entity).setTicksLeftOnBribe(ticksLeftOnBribe - 1);
                    } else if (ticksLeftOnBribe == 1) {
                        ((Mercenary) entity).setTicksLeftOnBribe(ticksLeftOnBribe - 1);
                        updateCharacter.removeAlly((MovingEntity) entity);
                        ((Mercenary) entity).setIsBribed(false);
                    }
                }
                if (updateCharacter.getInBattle() && !((MovingEntity) entity).getInBattle()) {
                    ((MovingEntity) entity).move(currentGame);
                }
                if (updateCharacter.getInBattle() && entity instanceof Mercenary
                        && !((MovingEntity) entity).getInBattle()) {
                    if (isMercenaryAdjacent(entity.getPos())) {
                        ((MovingEntity) entity).move(currentGame);
                    }
                }
                
                if (!updateCharacter.getInBattle() && !((MovingEntity) entity).getInBattle()) {
                    ((MovingEntity) entity).move(currentGame);
                    if (((MovingEntity) entity).isHostile() && updateCharacter.getPos().equals(entity.getPos())
                            && !currentGame.getDifficulty().equalsIgnoreCase("peaceful")) {
                        if (!loadedgame.getCharacter().getisInvisible()) {
                            updateCharacter.setInBattle(true);
                            ((MovingEntity) entity).setInBattle(true);
                        }
                    }
                }
                
                while (updateCharacter.getInBattle() && ((MovingEntity) entity).getInBattle()
                        && ((MovingEntity) entity).isHostile()) {
                    Entity removable = updateCharacter.doBattle(updateCharacter, (MovingEntity) entity, currentGame, toRemove);
                    if (removable != null){
                        toRemove.add(removable);
                    } 
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
                    ((ZombieToastSpawner) entity)
                            .setTicksSinceSpawn(((ZombieToastSpawner) entity).getTicksSinceSpawn() + 1);
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
        if (zombieToastSpawners.size() > 0) {
            for (Entity zombie : zombieToastSpawners) {
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
            RewindGame();
        }
        tick++;
        
        return new DungeonResponse(id, name, e, i, currentGame.getBuildables(), Goalstring);

    }

    /**
     * Interacts with a given entity Id
     * 
     * @param entityId
     * @return DungeonResponse
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        DungeonMania loadedgame = this.loadedgame;
        Entity interactableEntity = null;
        for (Entity entity : loadedgame.getEntities()) {
            if (entity.getId().equals(entityId)) {
                interactableEntity = entity;
            }
        }
        if (!(interactableEntity instanceof Mercenary) && !(interactableEntity instanceof ZombieToastSpawner)) {
            throw new IllegalArgumentException("not a mercenary or Zombie Spawner");
        }
        Entity bow = null;
        Entity sword = null;
        Entity treasure = null;
        Entity one_ring = null;
        Entity sceptre = null;
        for (Entity item : loadedgame.getItems()) {
            if (item instanceof Bow) {
                bow = item;
            } else if (item instanceof TreasureEntity || item instanceof SunStone) {
                treasure = item;
            } else if (item instanceof SwordEntity) {
                sword = item;
            } else if (item instanceof TheOneRingEntity) {
                one_ring = item;
            } else if (item instanceof Sceptre) {
                sceptre = item;
            }
        }

        Boolean bribeMPossible = (treasure != null) || (sceptre != null);
        Boolean bribeAPossible = (one_ring != null) || (sceptre != null);

        if (!bribeMPossible && interactableEntity instanceof Mercenary) {
            throw new InvalidActionException("insufficient material to bribe");
        }
        if (!bribeAPossible && interactableEntity instanceof Assassin) {
            throw new InvalidActionException("insufficient material to bribe");
        }
        if (interactableEntity instanceof Mercenary) {
            if (!isMercenaryAdjacent(interactableEntity.getPos()) && !RealisAdjacent(interactableEntity.getPos())) {
                throw new InvalidActionException("not cardinally adjacent within 2 squares");
            }
            if (sceptre != null) {
                ((Mercenary) interactableEntity).setTicksLeftOnBribe(10);
                Character updateCharacter = loadedgame.getCharacter();
                updateCharacter.addAlly((Mercenary) interactableEntity);
                loadedgame.setCharacter(updateCharacter);
                ((Mercenary) interactableEntity).setIsBribed(true);
            } else {
                if (interactableEntity instanceof Assassin) {
                    if (one_ring != null) {
                        loadedgame.removeItem(one_ring);
                        Character updateCharacter = loadedgame.getCharacter();
                        updateCharacter.addAlly((Assassin) interactableEntity);
                        loadedgame.setCharacter(updateCharacter);
                        ((Assassin) interactableEntity).setIsBribed(true);
                    }
                } else {
                    if (treasure instanceof TreasureEntity || treasure instanceof SunStone) {
                        if (treasure instanceof TreasureEntity) {
                            loadedgame.removeItem(treasure);
                        }
                        Character updateCharacter = loadedgame.getCharacter();
                        updateCharacter.addAlly((Mercenary) interactableEntity);
                        loadedgame.setCharacter(updateCharacter);
                        ((Mercenary) interactableEntity).setIsBribed(true);
                    }
                }
            }
        }
        if (interactableEntity instanceof ZombieToastSpawner) {
            if (!RealisAdjacent(interactableEntity.getPos())) {
                throw new InvalidActionException("not cardianally adjacent");
            }
            if (sword == null && bow == null) {
                throw new InvalidActionException("no weapon");
            }
            if (RealisAdjacent(interactableEntity.getPos()) && (sword != null || bow != null)) {
                loadedgame.removeEntity(interactableEntity);
            }
        }
        return new DungeonResponse(loadedgame.getId(), loadedgame.getName(), loadedgame.getEntityResponses(),
                loadedgame.getItemResponses(), null, GoalFactory.goalString(loadedgame.getGoal()));
    }

    /**
     * Checks if the character can craft a bow
     * 
     * @return boolean
     */
    public Boolean CheckBow() {
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
        if (wood >= 1 && arrows >= 3) {
            if (!dungeon.getBuildables().contains("bow")) {
                dungeon.addToBuildableEntities("bow");
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the character can craft a sceptre
     * 
     * @return boolean
     */
    public Boolean CheckSceptre() {
        int wood = 0;
        int arrows = 0;
        int keys = 0;
        int treasure = 0;
        int stone = 0;
        DungeonMania dungeon = this.loadedgame;
        for (Entity item : dungeon.getItems()) {
            if (item.getType().equals("wood")) {
                wood++;
            }
            if (item.getType().equals("arrow")) {
                arrows++;
            }
            if (item.getType().equals("keys")) {
                keys++;
            }
            if (item.getType().equals("treasure")) {
                treasure++;
            }
            if (item.getType().equals("sun_stone")) {
                stone++;
            }
        }
        if ((wood >= 1 || arrows >= 2) && (keys >= 1 || treasure >= 1) && stone >= 1) {
            if (!dungeon.getBuildables().contains("sceptre")) {
                dungeon.addToBuildableEntities("sceptre");
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the character can craft a sceptre
     * 
     * @return boolean
     */
    public Boolean CheckMidnightArmour() {
        boolean hasArmour = false;
        boolean hasZombie = false;
        int stone = 0;
        DungeonMania dungeon = this.loadedgame;
        for (Entity item : dungeon.getItems()) {
            if (item.getType().equals("armour")) {
                hasArmour = true;
            }
            if (item.getType().equals("sun_stone")) {
                stone++;
            }
        }
        for (Entity entity : dungeon.getEntities()) {
            if (entity instanceof ZombieToast) {
                hasZombie = true;
            }
        }
        if (hasArmour && stone >= 1 && !hasZombie) {
            if (!dungeon.getBuildables().contains("midnight_armour")) {
                dungeon.addToBuildableEntities("midnight_armour");
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the character can craft a shield
     * 
     * @return boolean
     */
    
    public Boolean CheckShield() {
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
        if ((treasure >= 1 || keys >= 1) && wood >= 1) {
            if (!dungeon.getBuildables().contains("shield")) {
                dungeon.addToBuildableEntities("shield");
            }
            return true;
        }
        return false;
    }

    /**
     * Builds a bow given the string of the type throws error if the desired craft
     * is not bow or shield
     * 
     * @param buildable
     * @return DungeonResponse
     * @throws IllegalArgumentException
     * @throws InvalidActionException
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        List<String> builds = new ArrayList<String>();
        builds.add("bow");
        builds.add("shield");
        builds.add("midnight_armour");
        builds.add("sceptre");
        if (!builds.contains(buildable)) {
            throw new IllegalArgumentException();
        }
        DungeonMania dungeon = this.loadedgame;
        if (buildable.equals("bow")) {
            if (CheckBow()) {
                dungeon.addBuildable("bow");
            } else {
                throw new InvalidActionException("cannot build bow");
            }
        }

        if (buildable.equals("shield")) {
            if (CheckShield()) {
                dungeon.addBuildable("shield");
            } else {
                throw new InvalidActionException("cannot build shield");
            }
        }

        if (buildable.equals("midnight_armour")) {
            if (CheckMidnightArmour()) {
                dungeon.addBuildable("midnight_armour");
            } else {
                throw new InvalidActionException("cannot build midnight armour");
            }
        }

        if (buildable.equals("sceptre")) {
            if (CheckSceptre()) {
                dungeon.addBuildable("sceptre");
            } else {
                throw new InvalidActionException("cannot build sceptre");
            }
        }

        return new DungeonResponse(loadedgame.getId(), loadedgame.getName(), loadedgame.getEntityResponses(),
                loadedgame.getItemResponses(), loadedgame.getBuildables(),
                GoalFactory.goalString(loadedgame.getGoal()));
    }

    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String gameMode)
            throws IllegalArgumentException {
        // checking gameMode
        Boolean peaceful = gameMode.equalsIgnoreCase("peaceful");
        Boolean standard = gameMode.equalsIgnoreCase("standard");
        Boolean hard = gameMode.equalsIgnoreCase("hard");
        if (!peaceful && !standard && !hard) {
            throw new IllegalArgumentException("invalid gamemode");
        }

        Position start = new Position(xStart, yStart);
        Position end = new Position(xEnd, yEnd);
        List<List<Boolean>> grid = Prims.generate(50, 50, start, end);
        DungeonMania dungeonMania = new DungeonMania(gameMode, "RandomDungeon");

        dungeonMania.createEntity(start, "player");
        dungeonMania.createEntity(end, "exit");

        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                if (!grid.get(i).get(j)) {
                    Position pos = new Position(i, j);
                    dungeonMania.createEntity(pos, "wall");
                }
            }
        }

        JSONObject jsonGoalCondition = new JSONObject("{\"goal\": \"exit\"}");
        dungeonMania.setGoal(GoalFactory.generate(jsonGoalCondition.toString()));
        List<EntityResponse> entityResponses = dungeonMania.getEntityResponses();
        dungeonMania.setId(Integer.toString(this.games.size() + 1));
        this.games.add(dungeonMania);
        this.loadedgame = dungeonMania;
        return new DungeonResponse(dungeonMania.getId(), "RandomDungeon", entityResponses, new ArrayList<>(),
                new ArrayList<>(), GoalFactory.goalString(dungeonMania.getGoal()));
    }

    /**
     * Writes the given contents to a given filename
     * 
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
    public DungeonResponse rewind(int ticks)
    throws IllegalArgumentException {
        List<ItemResponse> items = new ArrayList<>();
        List<String> buildables = new ArrayList<>();
        TimeTravelDuration = ticks;
        if (this.tick < ticks) {
            ticks = this.tick;
        }
        File f = new File("src/main/resources/rewindsaves/" + this.loadedgame.getDifficulty() + "-" + loadedgame.getName() + "-" + Integer.toString(tick-ticks) + "-" + ".json" );
        String s = f.getAbsolutePath();
        JSONObject dungeon = null;

        try {
            dungeon = new JSONObject(new JSONTokener(new FileReader(s)));
        } catch (Exception e) {
        }

        String difficulty = dungeon.getString("difficulty");
        String mapName = dungeon.getString("mapName");
        DungeonMania dungeonMania = new DungeonMania(difficulty, mapName);
        OlderPlayer player = null;
        JSONArray entities = dungeon.getJSONArray("entities");
        for (int i = 0; i < entities.length(); i++) {
            String type = entities.getJSONObject(i).getString("type");
            int x = entities.getJSONObject(i).getInt("x");
            int y = entities.getJSONObject(i).getInt("y");
            Position pos = new Position(x, y, 0); // placeholder for layer
            dungeonMania.createEntity(pos, type);
        }
        dungeonMania.setCharacter(this.loadedgame.getCharacter());
        dungeonMania.createEntity(dungeonMania.getCharacter().getPos(), dungeonMania.getCharacter().getType());
        
        JSONArray inventory = dungeon.getJSONArray("inventory");
        for (int i = 0; i < inventory.length(); i++) {
            String type = inventory.getJSONObject(i).getString("type");
            if(!type.equalsIgnoreCase("time_turner")) {
                for(Entity entity: dungeonMania.getEntities()) {
                    if (entity instanceof OlderPlayer) {
                    ((OlderPlayer) entity).AddItem(type, loadedgame);
                    }
                }
            }
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
     * Turns a given goal the a json file
     * 
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
     * 
     * @return DungeonMania
     */
    public DungeonMania getLoadedGame() {
        return this.loadedgame;
    }
    public void RewindGame() throws IllegalArgumentException {
        int numSaves = tick;
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
            if(type.equals("player")) {
                newEntity.put("type", "older_player");    
            }
            else {
            newEntity.put("type", type);
            }
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
            String pathname = "src/main/resources/rewindsaves/";
            String filename = difficulty + "-" + mapName + "-" + Integer.toString(numSaves) + "-" + ".json";
            File f = new File(pathname);
            f.mkdir();
            String absolutePathName = f.getAbsolutePath();
            File save = new File(absolutePathName + "/" + filename);
            save.createNewFile();
            writeToFile(conts, absolutePathName + "/" + filename);
        } catch (IOException e) {

        }

        String goalString = GoalFactory.goalString(this.loadedgame.getGoal());
    }
}