package dungeonmania;

import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
// import java.util.Random;

public class DungeonMania {
    private Character character;
    private int height;
    private int width;
    private List<Entity> Entities;
    private List<Entity> Items;
    private List<String> Buildables;
    private Goal goal;
    private String id;
    private int intId;
    private String name;
    private String difficulty;
    private List<SwampTile> swampTiles;
    private Position entryPosition;

    public DungeonMania(String difficulty, String name) {
        this.difficulty = difficulty;
        this.name = name;
        this.Entities = new ArrayList<>();
        this.Items = new ArrayList<>();
        this.Buildables = new ArrayList<>();
        this.intId = 0;
        this.swampTiles = new ArrayList<SwampTile>();
        // TODO this.random = new Random(29);
    }

    /**
     * Increments the id
     * 
     * @return int
     */
    public int incrementIntId() {
        return this.intId++;
    }

    /**
     * Sets difficulty of the game
     * 
     * @param difficulty
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Adds a given string to the list of buildable entities
     * 
     * @param s
     */
    public void addToBuildableEntities(String s) {
        this.Buildables.add((s));
    }

    /**
     * Gets the list of buildable entities
     * 
     * @return List<String>
     */
    public List<String> getBuildables() {
        return this.Buildables;
    }

    /**
     * Removes a given entity from items list
     * 
     * @param e
     */
    public void removeItem(Entity e) {
        this.Items.remove(e);
    }

    /**
     * Gets an instance of entity given the id
     * 
     * @param id
     * @return Entity
     */
    public Entity getItemFromId(String id) {
        for (Entity item : this.Items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;

    }

    /**
     * Given a string of the desired item, crafts the item and consumes necessary
     * materials
     * 
     * @param type
     */
    public void addBuildable(String type) {
        String id = Integer.toString(Buildables.size());
        List<Entity> toRemove = new ArrayList<>();
        if (type.equals("bow")) {
            this.Items.add(new Bow(null, type, id));
            this.Buildables.remove("bow");
            int woodCount = 1;
            int arrowCount = 3;
            for (Entity entity : this.Items) {
                if (entity instanceof WoodEntity && woodCount > 0) {
                    toRemove.add(entity);
                    woodCount--;
                }
                if (entity instanceof ArrowsEntity && arrowCount > 0) {
                    toRemove.add(entity);
                    arrowCount--;
                }
            }
        }
        if (type.equals("shield")) {
            this.Items.add(new Shield(null, type, id));
            this.Buildables.remove("shield");
            int metalCount = 1;
            int woodCount = 2;
            for (Entity entity : this.Items) {
                if (entity instanceof WoodEntity && woodCount > 0) {
                    toRemove.add(entity);
                    woodCount--;
                }
                if ((entity instanceof KeyEntity || entity instanceof TreasureEntity) && metalCount > 0) {
                    if (!(entity instanceof SunStone)) {
                        toRemove.add(entity);
                    }
                    metalCount--;
                }
            }
        }
        if (type.equals("midnight_armour")) {
            this.Items.add(new MidnightArmour(null, type, id));
            this.Buildables.remove("midnight_armour");
            int armourCount = 1;
            int stoneCount = 1;
            for (Entity entity : this.Items) {
                if (entity instanceof ArmourEntity && armourCount > 0) {
                    toRemove.add(entity);
                    armourCount--;
                }
                if ((entity instanceof SunStone) && stoneCount > 0) {
                    toRemove.add(entity);
                    stoneCount--;
                }
            }
        }
        if (type.equals("sceptre")) {
            this.Items.add(new Sceptre(null, type, id));
            this.Buildables.remove("sceptre");
            int metalCount = 1;
            int woodCount = 2;
            int stoneCount = 1;
            for (Entity entity : this.Items) {
                if (woodCount > 0) {
                    if (entity instanceof WoodEntity && woodCount % 2 == 0) {
                        toRemove.add(entity);
                        woodCount -= 2;
                    } else if (entity instanceof ArrowsEntity) {
                        toRemove.add(entity);
                        woodCount--;
                    }
                }
                if ((entity instanceof SunStone) && stoneCount > 0) {
                    toRemove.add(entity);
                    stoneCount--;
                } else if ((entity instanceof KeyEntity || entity instanceof TreasureEntity) && metalCount > 0) {
                    toRemove.add(entity);
                    metalCount--;
                }

            }
        }

        for (Entity entity : toRemove) {
            this.removeItem(entity);
        }
    }

    /**
     * Removes items that have either been consumed, or run out of durability from
     * the items list.
     */
    public void removeUsedItems() {
        List<Entity> useditems = new ArrayList<>();
        for (Entity item : this.Items) {
            if (item instanceof Weapons && ((Weapons) item).getDurability() == 0) {
                useditems.add(item);
            }
            if (item instanceof TheOneRingEntity && ((TheOneRingEntity) item).getIsUsed()) {
                useditems.add(item);
            }
        }
        for (Entity usedup : useditems) {
            this.Items.remove(usedup);
        }
    }

    /**
     * Gets the list of Items
     * 
     * @return List<Entity>
     */
    public List<Entity> getItems() {
        return Items;
    }

    /**
     * Sets the list of items to a specific list.
     * 
     * @param items
     */
    public void setItems(List<Entity> items) {
        Items = items;
    }

    public List<Position> getSwampTilePos() {
        List<Position> pos = new ArrayList<Position>();
        for (SwampTile tile : this.swampTiles) {
            pos.add(tile.getPos());
        }

        return pos;
    }

    public int getSlow(Position pos) {
        SwampTile tile = null;
        for (SwampTile tiles : this.swampTiles) {
            if (pos.equals(tiles.getPos())) {
                tile = tiles;
                break;
            }
        }

        if (tile == null) {
            return 1;
        }

        return tile.getFactor();
    }

    public void addSwampTile(SwampTile tile) {
        this.swampTiles.add(tile);
    }

    public void setSwampTiles(List<SwampTile> tiles) {
        this.swampTiles = tiles;
    }

    /**
     * Gets the difficulty of the game
     * 
     * @return String
     */
    public String getDifficulty() {
        return this.difficulty;
    }

    /**
     * Gets the item responses for all items in the items list
     * 
     * @return List<ItemResponse>
     */
    public List<ItemResponse> getItemResponses() {
        List<ItemResponse> items = new ArrayList<>();
        for (Entity item : this.Items) {
            items.add(new ItemResponse(item.getId(), item.getType()));
        }
        return items;
    }

    /**
     * Gets the instance of character in the game
     * 
     * @return Character
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * Sets the character
     * 
     * @param character
     */
    public void setCharacter(Character character) {
        this.character = character;
    }

    /**
     * Updates a given entity.
     * 
     * @param e
     */
    public void updateEntities(Entity e) {
        for (Entity entity : this.Entities) {
            if (entity.getId().equals(e.getId())) {
                entity = e;
            }
        }
    }

    /**
     * Gets the largest x value of the map
     * 
     * @return int
     */
    public int getLargestX() {
        int x = 0;
        for (Entity entity : this.Entities) {
            int newX = entity.getPos().getX();
            if (newX > x) {
                x = newX;
            }
        }
        return x;
    }

    /**
     * Gets the largest y value of the map
     * 
     * @return int
     */
    public int getLargestY() {
        int y = 0;
        for (Entity entity : this.Entities) {
            int newY = entity.getPos().getY();
            if (newY > y) {
                y = newY;
            }
        }
        return y;

    }

    /**
     * Gets the name of the dungeon
     * 
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the dungeon
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the id of the dungeon
     * 
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the dungeon
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the height of the dungeon
     * 
     * @return int
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the width of the dungeon
     * 
     * @return int
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns a list of entity responses for all entities in the game
     * 
     * @return List<EntityResponse>
     */
    public List<EntityResponse> getEntityResponses() {
        List<EntityResponse> e = new ArrayList<>();
        for (Entity entity : this.Entities) {
            e.add(new EntityResponse(entity.getId(), entity.getType(), entity.getPos(), entity.getIsInteractable()));
        }
        return e;
    }

    /**
     * Gets the List of entities
     * 
     * @return List<Entity>
     */
    public List<Entity> getEntities() {
        return this.Entities;
    }

    /**
     * Sets the list of entities in the game
     * 
     * @param entities
     */
    public void setEntities(List<Entity> entities) {
        Entities = entities;
    }

    /**
     * Gets the goal of the game
     * 
     * @return Goal
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Sets the goal of the game
     * 
     * @param goal
     */
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    /**
     * If a battle is won and an item is collected from the battle, add it to items
     * list
     * 
     * @param e
     */
    public void winItem(Entity e) {
        this.Items.add(e);
    }

    /**
     * Creates a random position within the map boundaries
     * 
     * @return Position
     */
    public Position generateRandomPos() {
        int spawnX = ThreadLocalRandom.current().nextInt(0, getLargestX() + 1);
        int spawnY = ThreadLocalRandom.current().nextInt(0, getLargestY() + 1);
        return new Position(spawnX, spawnY, 0);
    }

    /**
     * Spawns a spider
     */
    public void spawnSpider() {
        Boolean isBoulder = true;
        Position p = null;
        while (isBoulder) {
            p = generateRandomPos();
            for (Entity entity : this.Entities) {
                if (p.equals(entity.getPos()) && entity.getType().equals("boulder")) {
                    isBoulder = true;
                } else {
                    isBoulder = false;
                }
            }
            if (p.getX() == 0 && p.getY() == 0) {
                isBoulder = true;
            }
        }
        Spider s = new Spider(p, "spider", Integer.toString(this.incrementIntId()));
        Entities.add(s);

    }

    /**
     * Spawns a hydra
     */
    public void spawnHydra() {
        Position p = generateRandomPos();
        while (hasWall(this, p)) {
            p = generateRandomPos();
        }
        Hydra h = new Hydra(p, "hydra", Integer.toString(this.incrementIntId()));
        Entities.add(h);
    }

    /**
     * Spawns mercenary at entry position
     */
    public void spawnMercenary() {
        int chance = ThreadLocalRandom.current().nextInt(0, 4);
        Entity m;
        if (chance == 0) {
            m = new Assassin(entryPosition, "assassin", Integer.toString(this.incrementIntId()));
        } else {
            m = new Mercenary(entryPosition, "mercenary", Integer.toString(this.incrementIntId()));
        }
        Entities.add(m);
    }

    /**
     * Spawns a zombie given the position of the toast spawner
     * 
     * @param pos
     */
    public void spawnZombie(Position pos) {
        Boolean isWall = true;
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        Direction random = null;
        Position p = null;
        while (isWall) {
            isWall = false;
            random = directions.get(ThreadLocalRandom.current().nextInt(0, 3));
            for (Entity entity : this.Entities) {
                if ((entity instanceof Wall) && pos.translateBy(random).equals(entity.getPos())) {
                    isWall = true;
                }
            }
        }
        p = pos.translateBy(random);
        createEntity(p, "zombie_toast");
    }

    /**
     * Removes a specific entity from the Entities list
     * 
     * @param e
     */
    public void removeEntity(Entity e) {
        this.Entities.remove(e);
    }

    /**
     * Given a position and a string type, create an entity with respective class
     * and add to entities list
     * 
     * @param pos
     * @param Type
     */
    public void createEntity(Position pos, String Type) {
        String id = Integer.toString(this.incrementIntId());
        Entity entity = null;
        // Static entities
        if (Type.equalsIgnoreCase("wall")) {
            entity = new Wall(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("exit")) {
            entity = new Exit(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("boulder")) {
            entity = new Boulder(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("switch")) {
            entity = new FloorSwitch(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("door")) {
            entity = new DoorEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("zombie_toast_spawner")) {
            entity = new ZombieToastSpawner(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("treasure")) {
            entity = new TreasureEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("mercenary")) {
            int chance = ThreadLocalRandom.current().nextInt(100);
            if (chance < 25) {
                entity = new Assassin(pos, Type, id);
            } else {
                entity = new Mercenary(pos, Type, id);
            }
        }
        if (Type.equalsIgnoreCase("zombie_toast")) {
            entity = new ZombieToast(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("player")) {
            entity = new Character(pos, Type, id);
            this.entryPosition = pos;
            this.character = (Character) entity;
        }
        if (Type.equalsIgnoreCase("wood")) {
            entity = new WoodEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("arrow")) {
            entity = new ArrowsEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("key")) {
            entity = new KeyEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("health_potion")) {
            entity = new HealthPotionEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("invincibility_potion")) {
            entity = new InvincibilityPotionEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("invisibility_potion")) {
            entity = new InvisibilityPotionEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("bomb")) {
            entity = new BombEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("sword")) {
            entity = new SwordEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("armour")) {
            entity = new ArmourEntity(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("one_ring")) {
            entity = TheOneRingEntity.getInstance(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("sun_stone")) {
            entity = new SunStone(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("anduril")) {
            entity = new Anduril(pos, Type, id);
        }
        if (entity != null) {
            this.Entities.add(entity);
        }
    }

    /**
     * Given a string type, add respective class to items list
     * 
     * @param Type
     */
    public void AddItem(String Type) {
        String id = Integer.toString(this.incrementIntId());
        Entity entity = null;
        if (Type.equalsIgnoreCase("wood")) {
            entity = new WoodEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("arrow")) {
            entity = new ArrowsEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("key")) {
            entity = new KeyEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("treasure")) {
            entity = new TreasureEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("bomb")) {
            entity = new BombEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("sword")) {
            entity = new SwordEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("armour")) {
            entity = new ArmourEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("health_potion")) {
            entity = new HealthPotionEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("invincibility_potion")) {
            entity = new InvincibilityPotionEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("invisibility_potion")) {
            entity = new InvisibilityPotionEntity(null, Type, id);
        }
        if (Type.equalsIgnoreCase("one_ring")) {
            entity = TheOneRingEntity.getInstance(null, Type, id);
        }
        if (Type.equalsIgnoreCase("sun_stone")) {
            entity = new SunStone(null, Type, id);
        }
        if (Type.equalsIgnoreCase("anduril")) {
            entity = new Anduril(null, Type, id);
        }
        if (entity != null) {
            this.Items.add(entity);
        }

    }

    /**
     * Creates a portal given position type and colour of the portal
     * 
     * @param pos
     * @param Type
     * @param colour
     */
    public void createPortal(Position pos, String Type, String colour) {
        String id = Integer.toString(this.incrementIntId());
        Entity entity = new Portal(pos, Type, id);
        ((Portal) entity).setColour(colour);
        this.Entities.add(entity);
    }

    public Boolean hasWall(DungeonMania game, Position pos) {
        for (Entity entity : game.getEntities()) {
            if ((entity instanceof Wall) && pos.equals(entity.getPos())) {
                return true;
            }
        }
        return false;
    }
}
