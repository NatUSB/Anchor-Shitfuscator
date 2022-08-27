package wtf.krypton.util;

public class MultiThreading {

    public static void run(Runnable r) {
        Thread t = new Thread(r);
        t.start();
    }

}
