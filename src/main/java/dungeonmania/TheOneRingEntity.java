package dungeonmania;

import dungeonmania.util.Position;

public class TheOneRingEntity extends CollectableEntities {

    //private static TheOneRingEntity singleInstance = null;
    
    public TheOneRingEntity(Position pos, String type, String id) {
        super(pos, type, id);
        super.setIsInteractable(false);
    }

    /*public static synchronized TheOneRingEntity getInstance() {
        if (singleInstance == null) {
			singleInstance = new TheOneRingEntity();
		}
		return singleInstance;
    }*/
}

