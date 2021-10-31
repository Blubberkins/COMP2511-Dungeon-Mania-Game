package dungeonmania;

import dungeonmania.util.Position;

public class InvisibilityPotionEntity extends CollectableEntities {
    
    public InvisibilityPotionEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }
}
