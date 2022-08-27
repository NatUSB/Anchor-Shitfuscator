package wtf.krypton.gui.menu;

import wtf.krypton.gui.Window;

public abstract class Menu {

    public abstract void init();
    public abstract void draw(float mouseX, float mouseY, float delta, long windowID, Window window);
    public abstract void mouseClicked(float x, float y, int button);
    public abstract void mouseReleased(float x, float y, int button);
    public abstract void onScroll(double amount);
    public abstract void fileDropped(String path);

}
