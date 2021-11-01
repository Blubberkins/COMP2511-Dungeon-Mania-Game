package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.response.models.*;

import java.util.List;

// By Liam

public class TestMercenary {
    @Test
    public void testOptimalMove() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        int spider = -1;
        while (spider != 0){
            int spidercount = 0;
            dm.newGame("basicmap9", "Standard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if(e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;

        }
        Mercenary mercenary = findMercenary(game);
        assertTrue(mercenary != null);
        Character player = game.getCharacter();
        assertTrue(player != null);
        // maxing out the hp of the character so they don't die to random spiders
        
        

        Position mPos = mercenary.getPos();
        Position pPos = player.getPos();

        // after one move the mercenary should be closer
        // player started on (1, 1), mercenary on (7, 7)
        // so they are 12 steps away. the mercenary should cut it down to 10.
        dm.tick(null, Direction.RIGHT);
        
        
        mPos = mercenary.getPos();
        pPos = player.getPos();
        Position vector = Position.calculatePositionBetween(mPos, pPos);
        assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 10);
        // repeating...
        dm.tick(null, Direction.RIGHT);
        
        
        mPos = mercenary.getPos();
        pPos = player.getPos();
        vector = Position.calculatePositionBetween(mPos, pPos);
        assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 8);

        // changing direction
        for (int i = 0; i < 3; i++) {
            dm.tick(null, Direction.DOWN);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == (3 - i) * 2);
        }

        // extending this test for other orientations of motion
        mercenary.setPos(new Position(7, 1));
        player.setPos(new Position(1, 7));

        for (int i = 0; i < 2; i++) {
            dm.tick(null, Direction.RIGHT);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 2 * (5 - i));
        }

        for (int i = 0; i < 3; i++) {
            dm.tick(null, Direction.UP);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == (3 - i) * 2);
        }

        // and again
        mercenary.setPos(new Position(1, 1));
        player.setPos(new Position(7, 7));

        for (int i = 0; i < 2; i++) {
            dm.tick(null, Direction.LEFT);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 2 * (5 - i));
        }

        for (int i = 0; i < 3; i++) {
            dm.tick(null, Direction.UP);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == (3 - i) * 2);
        }

        // and again
        mercenary.setPos(new Position(1, 7));
        player.setPos(new Position(7, 1));

        for (int i = 0; i < 2; i++) {
            dm.tick(null, Direction.LEFT);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 2 * (5 - i));
        }

        for (int i = 0; i < 3; i++) {
            dm.tick(null, Direction.DOWN);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == (3 - i) * 2);
        }

    }

    @Test
    public void testOptimalMove2() {
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        int spider = -1;
        while (spider != 0){
            int spidercount = 0;
            dm.newGame("basicmap9", "Standard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if(e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;

        }
        Mercenary mercenary = findMercenary(game);
        assertTrue(mercenary != null);
        Character player = game.getCharacter();
        assertTrue(player != null);
        // maxing out the hp of the character so they don't die to random spiders
        
        

        Position mPos = mercenary.getPos();
        Position pPos = player.getPos();

        // after one move the mercenary should be closer
        // player started on (1, 1), mercenary on (7, 7)
        // so they are 12 steps away. the mercenary should cut it down to 10.
        dm.tick(null, Direction.DOWN);
        
        
        mPos = mercenary.getPos();
        pPos = player.getPos();
        Position vector = Position.calculatePositionBetween(mPos, pPos);
        assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 10);

        // repeating...
        dm.tick(null, Direction.DOWN);
        
        
        mPos = mercenary.getPos();
        pPos = player.getPos();
        vector = Position.calculatePositionBetween(mPos, pPos);
        assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 8);

        // changing direction
        for (int i = 0; i < 3; i++) {
            dm.tick(null, Direction.RIGHT);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == (3 - i) * 2);
        }

        // extending this test for other orientations of motion
        mercenary.setPos(new Position(7, 1));
        player.setPos(new Position(1, 7));

        for (int i = 0; i < 2; i++) {
            dm.tick(null, Direction.UP);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 2 * (5 - i));
        }

        for (int i = 0; i < 3; i++) {
            dm.tick(null, Direction.RIGHT);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == (3 - i) * 2);
        }

        // and again
        mercenary.setPos(new Position(1, 1));
        player.setPos(new Position(7, 7));

        for (int i = 0; i < 2; i++) {
            dm.tick(null, Direction.UP);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 2 * (5 - i));
        }

        for (int i = 0; i < 3; i++) {
            dm.tick(null, Direction.LEFT);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == (3 - i) * 2);
        }

        // and again
        mercenary.setPos(new Position(1, 7));
        player.setPos(new Position(7, 1));

        for (int i = 0; i < 2; i++) {
            dm.tick(null, Direction.DOWN);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == 2 * (5 - i));
        }

        for (int i = 0; i < 3; i++) {
            dm.tick(null, Direction.LEFT);
            
            
            mPos = mercenary.getPos();
            pPos = player.getPos();
            vector = Position.calculatePositionBetween(mPos, pPos);
            assertTrue(Math.abs(vector.getX()) + Math.abs(vector.getY()) == (3 - i) * 2);
        }

    }

    @Test
    public void testDumbMercenary() {
        // making the mercenary walk into walls, attack a non-existent player, etc.
        DungeonManiaController dm = new DungeonManiaController();
        DungeonMania game = null;
        int spider = -1;
        while (spider != 0){
            int spidercount = 0;
            dm.newGame("basicmap10", "Standard");
            game = dm.getLoadedGame();
            List<Entity> entities = game.getEntities();
            for (Entity e : entities) {
                if(e instanceof Spider) {
                    spidercount++;
                }
            }
            spider = spidercount;

        }

        Mercenary mercenary = findMercenary(game);
        assertTrue(mercenary != null);
        Character player = game.getCharacter();
        assertTrue(player != null);
        // maxing out the hp of the character so they don't die to random spiders
        
        

        // mercenary should be stuck
        for (int i = 0; i < 20; i++) {
            dm.tick(null, Direction.RIGHT);
            assertTrue(mercenary.getPos().getX() == 7);
            
            
            dm.tick(null, Direction.LEFT);
            assertTrue(mercenary.getPos().getX() == 7);
            
            
            dm.tick(null, Direction.UP);
            assertTrue(mercenary.getPos().getX() == 7);
            
            
            dm.tick(null, Direction.DOWN);
            assertTrue(mercenary.getPos().getX() == 7);
            
            
        }

        // attacking a non-existent player, mercenary should stay put
        Position curr = mercenary.getPos();
        game.setCharacter(null);
        player = game.getCharacter();

        assertTrue(curr == mercenary.getPos());
    }

    public Mercenary findMercenary(DungeonMania game) {
        // tracks the mercenary, there is only one
        List<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity.getType().compareTo("mercenary") == 0) {
                return (Mercenary) entity;
            }
        }

        return null;
    }
}