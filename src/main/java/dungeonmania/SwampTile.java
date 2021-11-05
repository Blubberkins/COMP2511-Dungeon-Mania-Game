package dungeonmania;

import dungeonmania.util.Position;

public class SwampTile {
    private Position pos;
    private int movement_factor;

    public SwampTile(Position position) {
        super();
        this.pos = position;
        this.movement_factor = 2;
    }

    public SwampTile(Position position, int movement_factor) {
        super();
        this.pos = position;
        this.movement_factor = movement_factor;
    }

    public Position getPos() {
        return this.pos;
    }

    public int getFactor() {
        return this.movement_factor;
    }
}