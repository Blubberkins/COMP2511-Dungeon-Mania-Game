package dungeonmania;

// goal interface
// By Liam

public interface Goal {
    /**
     * Gets the name of the goal
     * @return String
     */
    public String getName();

    /**
     * Checks if the goal has been completed
     * @param game
     * @return boolean
     */
    public Boolean isComplete(DungeonMania game);

    /**
     * Adds a goal.
     * @param g
     * @return boolean
     */
    public Boolean add(Goal g);

    /**
     * Removes a specific goal
     * @param g
     * @return boolean
     */
    public Boolean remove(Goal g);

}