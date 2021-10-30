package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoorEntity extends dungeonmania.StaticEntity {
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