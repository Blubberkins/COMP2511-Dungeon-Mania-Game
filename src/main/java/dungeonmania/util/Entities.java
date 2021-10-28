package dungeonmania.util;

import javax.swing.text.html.parser.Entity;

// updating for position in moving entities 
//updating for existence of entity in GUI
// after one tick, check spider is in the position
 public abstract class Entities {
    private Boolean exists;
    private Position pos;
    
    public Entities(){
        this.exists = true;
    }
    public void destroyEntity(){
        this.exists = false;
    }

    public void getX(){

    } 
    public void getY(){

    }

}
