package dungeonmania;

import dungeonmania.util.Position;

public class DoorEntity extends StaticEntity {
    private Boolean isOpen;
    
    public DoorEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }

    public Boolean getIsOpen() {
        return isOpen;
    }
    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }
}