package dungeonmania;

// goal interface
// By Liam

public interface Goal {
    public String getName();

    public Boolean isComplete(DungeonMania game);

    public Boolean add(Goal g);

    public Boolean remove(Goal g);

}