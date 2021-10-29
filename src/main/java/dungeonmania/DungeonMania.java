package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.io.IOException;
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
    private List<EntityResponse> entityResponses;
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
        this.entityResponses = new ArrayList<>();
    }
    
    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }
    public void updateEntities(Entity e){
        for (Entity entity: this.Entities) {
            if (entity.getId().equals(e.getId())){
                entity = e;
            }
        }
    }
    public int getLargestX(){
        int x = 0;
        for (Entity entity: this.Entities){
            int newX = entity.getPos().getX();
            if (newX > x){
                x = newX;
            }
        }
        return x;
    }
    public int getLargestY(){
        int y = 0;
        for (Entity entity: this.Entities){
            int newY = entity.getPos().getY();
            if (newY > y){
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
        int spawnX = ThreadLocalRandom.current().nextInt(0, getLargestX());
        int spawnY = ThreadLocalRandom.current().nextInt(0, getLargestY());
        return new Position (spawnX,spawnY,0);
    }
    public void spawnSpider(){
        Boolean isBoulder = true;
        Position p = null;
        while (isBoulder){
            p = generateRandomPos();
            for (Entity entity: this.Entities) {
              if (p.equals(entity.getPos()) && entity.getType().equals("boulder")){
                  isBoulder = true;
              }
              else {
                  isBoulder = false;
              }
            }
        }
     Spider s  = new Spider(p, "spider", Integer.toString(Entities.size() + 1));
     Entities.add(s);

    }
    public void createEntity(Position pos, String Type) {
        String id = Integer.toString(this.Entities.size());
        Entity entity = null;
        if (Type.equalsIgnoreCase("wall")) {
            entity = new Wall(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("exit")) {
            entity = new Exit(pos, Type, id);
        }
        if (Type.equalsIgnoreCase("player")) {
            entity = new Character(pos, Type, id);
            this.character = (Character) entity;
        }

        if (entity != null) {
            this.Entities.add(entity);
        }

    }
}
