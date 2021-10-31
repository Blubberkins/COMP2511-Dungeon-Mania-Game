package dungeonmania;

import dungeonmania.util.Position;

public class Portal extends StaticEntity {
    private String colour;

    public Portal(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
    
}
