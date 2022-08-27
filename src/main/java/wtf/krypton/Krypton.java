package wtf.krypton;

import wtf.krypton.gui.Window;
import wtf.krypton.gui.menu.impl.ObfuscateMenu;
import wtf.krypton.obfuscator.Obfuscator;
import wtf.krypton.obfuscator.transformers.impl.*;
import wtf.krypton.util.MultiThreading;
import wtf.krypton.util.font.FontManager;

import java.nio.file.Paths;

public class Krypton {

    public static Krypton instance = new Krypton();

    public Window window;

    public FontManager fontRenderer;

    public Obfuscator o;

    public boolean obfuscating = false;

    public int obfuscatedClasses = 0;

    public void start() {
        o = new Obfuscator();
        ObfuscateMenu.transformerAmount = 5;
    }

    public void shutdown() {

    }

    public void obfuscate(String input, String output) {
        MultiThreading.run(() -> {
            if(!obfuscating) {
                long start = System.currentTimeMillis();
                obfuscating = true;

                o.apply(new ClassRenamer());
                o.apply(new FieldRenamer());
                o.apply(new FullAccess());
//                o.apply(new MethodRenamer());
                o.apply(new Shuffle());
                o.apply(new StripDebugInformation());

                o.write(Paths.get(output));

                System.out.println("Finished in " + (System.currentTimeMillis() - start) + "ms");

                obfuscating = false;
            }
        });
    }

    public static void main(String[] args) {
        instance.start();
        instance.window = Window.get();
        instance.window.run();
        instance.shutdown();
    }

}
