package dungeonmania;

import dungeonmania.response.models.DungeonResponse;

// goal interface
// By Liam

public interface Goal {
    public String getName();

    public Boolean isComplete(DungeonResponse game);

    public Boolean add(Goal g);

    public Boolean remove(Goal g);

}