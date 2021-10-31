package dungeonmania;

import dungeonmania.util.Position;

public class TheOneRingEntity extends CollectableEntities {
    private static TheOneRingEntity single_instance = null;

    private TheOneRingEntity(Position pos, String type, String id) {
        super(pos, type, id);
        // TODO Auto-generated constructor stub
    }

    public static synchronized TheOneRingEntity getInstance(Position pos, String type, String id) {
        if (single_instance == null) {

            single_instance = new TheOneRingEntity(pos, type, id);
        }
        return single_instance;
    }
}
