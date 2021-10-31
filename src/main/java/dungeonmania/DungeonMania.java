package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;
import dungeonmania.*;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DungeonMania {
    private Character character;
    private int height;
    private int width;
    private List<Entity> Entities;
    private List<Entity> Items;
    private List<Entity> Buildables;
    private Goal goal;
    private String id;
    private String name;
    private String difficulty;

    public DungeonMania(String difficulty, String name) {
        this.difficulty = difficulty;
        this.name = name;
        this.Entities = new ArrayList<>();
        this.Items = new ArrayList<>();
        this.Buildables = new ArrayList<>();
    }

    public void addBuildable(String type) {
        String id = Integer.toString(Buildables.size());
        List<Entity> toRemove = new ArrayList<>();
        if (type.equals("bow")) {
            this.Buildables.add(new Bow(null, type, id));
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
            this.Buildables.add(new Shield(null, type, id));
            int metalCount = 1;
            int woodCount = 2;
            for (Entity entity : this.Items) {
                if (entity instanceof WoodEntity && woodCount > 0) {
                    toRemove.add(entity);
                    woodCount--;
                }
                if ((entity instanceof KeyEntity || entity instanceof TreasureEntity) && metalCount > 0) {
                    toRemove.add(entity);
                    metalCount--;
                }
            }
        }
        for (Entity entity : toRemove) {
            this.removeEntity(entity);
        }
    }
    public void removeBuildable(Entity e) {
        this.Buildables.remove(e);
    }
    public void removeUsedItems(){
        List<Entity> useditems = new ArrayList<>();
        for (Entity item: this.Items) {
            if (item instanceof Weapons && ((Weapons) item).getDurability() == 0){
                useditems.add(item);
            }
        }
        for (Entity usedup: useditems){
            this.Items.remove(usedup);
        }
    }

    public List<Entity> getItems() {
        return Items;
    }

    public void setItems(List<Entity> items) {
        Items = items;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public List<ItemResponse> getItemResponses() {
        List<ItemResponse> items = new ArrayList<>();
        for (Entity item : this.Items) {
            items.add(new ItemResponse(item.getId(), item.getType()));
        }
        return items;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public void updateEntities(Entity e) {
        for (Entity entity : this.Entities) {
            if (entity.getId().equals(e.getId())) {
                entity = e;
            }
        }
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public List<EntityResponse> getEntityResponses() {
        List<EntityResponse> e = new ArrayList<>();
        for (Entity entity : this.Entities) {
            e.add(new EntityResponse(entity.getId(), entity.getType(), entity.getPos(), entity.getIsInteractable()));
        }
        return e;

    }

    public List<Entity> getEntities() {
        return this.Entities;
    }

    public void setEntities(List<Entity> entities) {
        Entities = entities;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
    public Position generateRandomPos(){
        int spawnX = ThreadLocalRandom.current().nextInt(0, getLargestX() + 1);
        int spawnY = ThreadLocalRandom.current().nextInt(0, getLargestY() + 1);
        return new Position (spawnX,spawnY,0);
    }

    public void spawnSpider() {
        Boolean isBoulder = true;
        Position p = null;
        while (isBoulder) {
            p = generateRandomPos();
            for (Entity entity: this.Entities) {
                if (p.equals(entity.getPos()) && entity.getType().equals("boulder")){
                    isBoulder = true;
                }
                else {
                    isBoulder = false;
                }
            }
            if (p.getX() == 0 && p.getY() == 0) {
                isBoulder = true;
            }
        }
        Spider s = new Spider(p, "spider", Integer.toString(Entities.size() + 1));
        Entities.add(s);

    }

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
            random = directions.get(ThreadLocalRandom.current().nextInt(0, 3));
            for (Entity entity: this.Entities) {
                if (!(entity instanceof Wall) && pos.translateBy(random).equals(entity.getPos())) {
                    isWall = false;
                }
            }
        }
        p = pos.translateBy(random);
        createEntity(pos, "zombie_toast");
    }

    public void removeEntity(Entity e) {
        this.Entities.remove(e);
    }

    public void createEntity(Position pos, String Type) {
        String id = Integer.toString(this.Entities.size());
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
            entity = new Mercenary(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("zombie_toast")) {
            entity = new ZombieToast(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("player")) {
            entity = new Character(pos, Type, id);
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
            entity = new TheOneRingEntity(pos, Type, id);
        }
        if (entity != null) {
            this.Entities.add(entity);
        }
    }

    public void AddItem(String Type) {
        String id = Integer.toString(this.Entities.size());
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
            entity = new TheOneRingEntity(null, Type, id);
        }
        if (entity != null) {
            this.Items.add(entity);
        }

    }

    public void createPortal (Position pos, String Type, String colour) {
        String id = Integer.toString(this.Entities.size());
        Entity entity = new Portal(pos, Type, id);
        ((Portal) entity).setColour(colour);
        this.Entities.add(entity);
    }

    public void addEntity(String id2) {
    }
}
