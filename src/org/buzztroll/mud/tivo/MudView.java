package org.buzztroll.mud.tivo;

public abstract class MudView {

    protected Mud mud;
    
    public MudView(Mud mud) {
        this.mud = mud;
    }
    
    abstract public boolean handleKeyPress(int code, long rawcode);
    
    abstract public void repaintScreen();
    
    abstract public void clearScreen();

    abstract public void switchScreen();
    
}
