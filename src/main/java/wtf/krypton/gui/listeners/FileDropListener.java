package wtf.krypton.gui.listeners;

import wtf.krypton.gui.Window;

import static org.lwjgl.system.MemoryUtil.*;

public class FileDropListener {

    public static void onDrop(long window, int count, long names) {
        Window.currentMenu.fileDropped(memUTF8(memByteBufferNT1(memPointerBuffer(names, count).get(0))));
    }

}
