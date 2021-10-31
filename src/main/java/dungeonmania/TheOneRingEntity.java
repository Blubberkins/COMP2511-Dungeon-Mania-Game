package dungeonmania;

import dungeonmania.util.Position;

public class TheOneRingEntity extends CollectableEntities {
    private static TheOneRingEntity single_instance = null;
    private Boolean isUsed;

    private TheOneRingEntity(Position pos, String type, String id) {
        super(pos, type, id);
        this.isUsed = false;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public static synchronized TheOneRingEntity getInstance(Position pos, String type, String id) {
        if (single_instance == null) {

            single_instance = new TheOneRingEntity(pos, type, id);
        }
        return single_instance;
    }
}
