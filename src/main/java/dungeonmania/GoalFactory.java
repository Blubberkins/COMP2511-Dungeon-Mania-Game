package dungeonmania;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;

// string to goals
// assumes that the string passed in is valid
// By Liam

public class GoalFactory {
    public static Goal generate(String goals) {
        Goal newGoal = null;

        // assumes by contract that it can in fact be converted to json
        JSONObject goalJSON = new JSONObject(goals);

        if (goalJSON.has("goal")) {
            Object goal = goalJSON.get("goal");
            if (goal instanceof String) {
                switch ((String) goal) {
                case "AND":
                    newGoal = new GoalComposite("AND");
                    addSubGoal(newGoal, goalJSON);
                    break;
                case "OR":
                    newGoal = new GoalComposite("OR");
                    addSubGoal(newGoal, goalJSON);
                    break;
                case "enemies":
                    newGoal = new EnemyGoal();
                    break;
                case "treasure":
                    newGoal = new TreasureGoal();
                    break;
                case "boulders":
                    newGoal = new BoulderGoal();
                    break;
                case "exit":
                    newGoal = new ExitGoal();
                    break;
                }
            }
        }

        return newGoal;
    }

    public static void addSubGoal(Goal goal, JSONObject jsonObject) {
        JSONArray subgoals = jsonObject.getJSONArray("subgoals");

        for (int i = 0; i < subgoals.length(); i++) {
            String subgoal = (String) subgoals.get(i);
            goal.add(generate(subgoal));
        }
    }

    public static String goalString(Goal goal) {
        // do a pretty print

        String string = "";

        if (goal instanceof GoalComposite) {
            List<Goal> subgoals = ((GoalComposite) goal).getSubgoals();

            for (int i = 0; i < subgoals.size(); i++) {
                string += ":" + (subgoals.get(i)).getName();
                if (i != subgoals.size() - 1) {
                    string += goal.getName();
                }
            }
        } else if (goal instanceof GoalLeaf) {
            string += ":" + goal.getName();
        }

        return string;
    }
}
