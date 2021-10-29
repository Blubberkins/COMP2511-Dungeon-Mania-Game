package dungeonmania;

import java.util.List;
import java.util.ArrayList;

// for logical operators on goals
// By Liam 

public class GoalComposite implements Goal {
    private String name;
    private List<Goal> subgoals = new ArrayList<Goal>();

    public GoalComposite(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Boolean isComplete(DungeonMania game) {
        switch (name) {
        case "AND":
            for (Goal goal : subgoals) {
                if (!(goal.isComplete(game))) {
                    return false;
                }
            }
            return true;
        case "OR":
            for (Goal goal : subgoals) {
                if (goal.isComplete(game)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public Boolean add(Goal g) {
        subgoals.add(g);
        return true;
    }

    public Boolean remove(Goal g) {
        subgoals.remove(g);
        return true;
    }

    public List<Goal> getSubgoals() {
        return this.subgoals;
    }

    public void setName(String name) {
        this.name = name;
    }
}
