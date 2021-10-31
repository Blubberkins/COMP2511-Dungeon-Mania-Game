package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;
import dungeonmania.response.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spider extends MovingEntity {
    private Position spawn_point;


    public Spider(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        this.spawn_point = pos;
        this.setPos(pos.translateBy(Direction.UP));
        super.setHealth(30);
        super.setDamage(5);
    }
    public void move(DungeonMania dungeonmania) {
        Position newPosition = nextSquare(dungeonmania);
        if (newPosition == null) {
            newPosition = previousSquare(dungeonmania);
        }

        if (newPosition != null) {
            this.setPos(newPosition);
        }
    }

    public Position nextSquare(DungeonMania dungeonmania) {
        int spawnX = this.spawn_point.getX();
        int spawnY = this.spawn_point.getY();
        int currX = this.getPos().getX();
        int currY = this.getPos().getY();
        Position newPos = null;
        if (currY < spawnY && currX <= spawnX) {
            newPos = (this.getPos().translateBy(Direction.RIGHT));
        } else if (currY > spawnY && currX >= spawnX) {
            newPos = (this.getPos().translateBy(Direction.LEFT));
        } else if (currY >= spawnY && currX <= spawnX) {
            newPos = (this.getPos().translateBy(Direction.UP));
        } else if (currY <= spawnY && currX > spawnX) {
            newPos = (this.getPos().translateBy(Direction.DOWN));
        }

        if (BoulderGoal.hasBoulder(dungeonmania, newPos)) {
            newPos = null;
        }

        return newPos;
    }

    public Position previousSquare(DungeonMania dungeonmania) {
        int spawnX = this.spawn_point.getX();
        int spawnY = this.spawn_point.getY();
        int currX = this.getPos().getX();
        int currY = this.getPos().getY();
        Position newPos = null;
        if (currY < spawnY && currX <= spawnX) {
            newPos = (this.getPos().translateBy(Direction.LEFT));
        } else if (currY > spawnY && currX >= spawnX) {
            newPos = (this.getPos().translateBy(Direction.RIGHT));
        } else if (currY == spawnY && currX <= spawnX) {
            newPos = (this.getPos().translateBy(Direction.DOWN));
        } else if (currY == spawnY && currX > spawnX) {
            newPos = (this.getPos().translateBy(Direction.UP));
        }

        if (BoulderGoal.hasBoulder(dungeonmania, newPos)) {
            newPos = null;
        }

        return newPos;
    }
    @Override
    public void receiveDMG(int damage) {
        super.setHealth(super.getHealth() - super.getDamage());
    }

}
