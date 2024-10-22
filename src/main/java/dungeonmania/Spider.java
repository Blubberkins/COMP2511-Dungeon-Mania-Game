package dungeonmania;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Spider extends MovingEntity {
    private Position spawn_point;
    private Damage damage;
    Boolean clockwise;

    public Spider(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
        this.spawn_point = pos;
        this.clockwise = true;
        this.setPos(pos.translateBy(Direction.UP));
        this.clockwise = true;
        super.setHealth(30);
        this.damage = new BaseDamage(5, null, null, null);
    }
    
    public void setDamage(int damage) {
        this.damage.setDamage(damage);
    }

    public Damage getDamage() {
        return this.damage;
    }
    /**
     * Checks if the current spider's desired move moves towards the character.
     * 
     * @param originalPos
     * @param newPos
     * @param dungeonmania
     * @return boolean
     */
    public Boolean IsCloser(Position originalPos, Position newPos, DungeonMania dungeonmania) {
        Position CharacterPos = dungeonmania.getCharacter().getPos();
        int CharacterX = CharacterPos.getX();
        int CharacterY = CharacterPos.getY();
        int OldPosX = originalPos.getX();
        int OldPosY = originalPos.getY();
        int NewPosX = originalPos.getX();
        int NewPosY = originalPos.getY();
        int OldDistance = Math.abs((OldPosX - CharacterX)) + Math.abs((OldPosY - CharacterY));
        int NewDistance = Math.abs((NewPosX - CharacterX)) + Math.abs((NewPosY - CharacterY));
        return (NewDistance > OldDistance);
    }

    /**
     * Calculates movement for spider
     */
    @Override
    public void move(DungeonMania dungeonmania) {
        Position newPosition = this.getPos();
        if (this.clockwise) {
            newPosition = nextSquare(dungeonmania);
        } else {
            newPosition = previousSquare(dungeonmania);
        }

        if (newPosition == null) {
            if (this.clockwise) {
                newPosition = previousSquare(dungeonmania);
                setDirection(false);
            } else {
                newPosition = nextSquare(dungeonmania);
                setDirection(true);
            }

        }

        if (newPosition != null) {
            this.setPos(newPosition);
        }
    }

    /**
     * Gets the next spiders next square and returns it
     * 
     * @param dungeonmania
     * @return Position
     */
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
        } else {
            newPos = (this.getPos().translateBy(Direction.DOWN));
        }
        if (dungeonmania.getCharacter().getisInvincible() && IsCloser(this.getPos(), newPos, dungeonmania)) {
            newPos = null;
        }
        if (BoulderGoal.hasBoulder(dungeonmania, newPos)) {
            newPos = null;
        }

        return newPos;
    }

    /**
     * Gets the spiders previous square and returns it
     * 
     * @param dungeonmania
     * @return Position
     */
    public Position previousSquare(DungeonMania dungeonmania) {
        int spawnX = this.spawn_point.getX();
        int spawnY = this.spawn_point.getY();
        int currX = this.getPos().getX();
        int currY = this.getPos().getY();
        Position newPos = null;
        if (currY < spawnY && currX <= spawnX) {
            newPos = (this.getPos().translateBy(Direction.DOWN));
        } else if (currY > spawnY && currX >= spawnX) {
            newPos = (this.getPos().translateBy(Direction.LEFT));
        } else if (currY >= spawnY && currX <= spawnX) {
            newPos = (this.getPos().translateBy(Direction.RIGHT));
        } else {
            newPos = (this.getPos().translateBy(Direction.UP));
        }
        if (dungeonmania.getCharacter().getisInvincible() && IsCloser(this.getPos(), newPos, dungeonmania)) {
            newPos = null;
        }

        if (BoulderGoal.hasBoulder(dungeonmania, newPos)) {
            newPos = null;
        }

        return newPos;
    }

    @Override
    public void receiveDMG(int damage) {
        super.setHealth(super.getHealth() - getDamage().calculateDamage());
    }

    public void setDirection(Boolean direction) {
        this.clockwise = direction;
    }

}
