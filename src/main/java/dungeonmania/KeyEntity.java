package dungeonmania;

import dungeonmania.util.Position;

public class KeyEntity extends CollectableEntities {
    private Boolean IsUsed;
    public KeyEntity(Position pos, String type, String id) {
        super(pos, type, id);
    }
    public Boolean getIsUsed() {
        return IsUsed;
    }
    public void setIsUsed(Boolean isUsed) {
        IsUsed = isUsed;
    }
    
}
