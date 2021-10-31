package dungeonmania;

import dungeonmania.util.Position;

public class HealthPotionEntity extends CollectableEntities {

    public HealthPotionEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(true);
    }
}
