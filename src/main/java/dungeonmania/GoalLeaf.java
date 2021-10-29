package dungeonmania;

// for literal goals
// By Liam

public class GoalLeaf implements Goal {
    private String name;

    public GoalLeaf(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    // this is a stub. functionality delegated to subclasses
    public Boolean isComplete(DungeonMania game) {
        return false;
    }

    // adding to a leaf does nothing
    public Boolean add(Goal g) {
        return false;
    }

    public Boolean remove(Goal g) {
        return false;
    }

}
