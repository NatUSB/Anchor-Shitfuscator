package wtf.krypton.gui.menu.impl;

import wtf.krypton.Krypton;
import wtf.krypton.gui.Window;
import wtf.krypton.gui.menu.Menu;
import wtf.krypton.obfuscator.Obfuscator;
import wtf.krypton.util.DrawingUtil;
import wtf.krypton.util.HoverAnimation;
import wtf.krypton.util.HoveredUtil;
import wtf.krypton.util.MathUtil;

import java.io.File;
import java.nio.file.Paths;

public class ObfuscateMenu extends Menu {

    private String input = "";
    private HoverAnimation obfAni;

    private int classes;
    private boolean startObf;

    public static int transformerAmount = 0;

    @Override
    public void init() {
        obfAni = new HoverAnimation(Window.get().getWidth()/2f - 150, 300, 300, 50, 1000);
        classes = 0;
        startObf = false;
    }

    @Override
    public void draw(float mouseX, float mouseY, float delta, long windowID, Window window) {
        if(!input.isEmpty()) {
            if(!Krypton.instance.obfuscating && startObf) {
                Obfuscator o = new Obfuscator();
                o.supply(Paths.get(input));
                Krypton.instance.o = o;
                classes = Krypton.instance.o.getClassAmount();
                Krypton.instance.obfuscate(input, input.substring(0, input.length() - 4) + "-obf.jar");
                startObf = false;
            }

            if(Krypton.instance.obfuscating) {
                //System.out.println(((Krypton.instance.obfuscatedClasses/classes)*transformerAmount)*100);
            }

            obfAni.update(mouseX, mouseY);

            Krypton.instance.fontRenderer.getFont("varela 20").drawCenteredString("Input: " + new File(input).getName(), window.getWidth()/2f, 160, -1);
            Krypton.instance.fontRenderer.getFont("varela 20").drawCenteredString("Output: " + new File(input).getName().substring(0, new File(input).getName().length() - 4) + "-obf.jar", window.getWidth()/2f, 190, -1);

            DrawingUtil.drawRoundedRect(window.getWidth()/2f - 150, 300, 300, 50, (float) MathUtil.interpolate(10, 20, obfAni.getOutput()), (float) MathUtil.interpolate(10, 20, obfAni.getOutput()), 0xff219aeb);
            Krypton.instance.fontRenderer.getFont("varela 40").drawCenteredString("Obfuscate", window.getWidth()/2f, 325 - Krypton.instance.fontRenderer.getFont("varela 40").getHeight("Obfuscate"), -1);
        } else {
            Krypton.instance.fontRenderer.getFont("varela 20").drawCenteredString("Drag and drop the file to be obfuscated!", window.getWidth()/2f, 100, -1);
        }
    }

    @Override
    public void mouseClicked(float x, float y, int button) {
        if(button == 0) {
            if(HoveredUtil.isRectHovered(Window.get().getWidth()/2f - 150, 300, 300, 50, x, y)) {
                startObf = true;
            }
        }
    }

    @Override
    public void mouseReleased(float x, float y, int button) {

    }

    @Override
    public void onScroll(double amount) {

    }

    @Override
    public void fileDropped(String path) {
        if(path.endsWith(".jar")) {
            input = path;
        }
    }

}
