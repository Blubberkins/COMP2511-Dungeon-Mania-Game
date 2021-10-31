package dungeonmania;

import dungeonmania.util.Position;

public class InvincibilityPotionEntity extends CollectableEntities {
    
    public InvincibilityPotionEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }
}
