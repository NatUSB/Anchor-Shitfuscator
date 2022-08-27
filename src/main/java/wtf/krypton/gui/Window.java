package wtf.krypton.gui;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import wtf.krypton.Krypton;
import wtf.krypton.gui.listeners.FileDropListener;
import wtf.krypton.gui.listeners.KeyListener;
import wtf.krypton.gui.listeners.MouseListener;
import wtf.krypton.gui.menu.Menu;
import wtf.krypton.gui.menu.impl.ObfuscateMenu;
import wtf.krypton.util.GLFWUtil;
import wtf.krypton.util.font.FontManager;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width, height;
    private String title;

    public static int screenWidth, screenHeight;

    public static long glfwWindow;

    private static Window window = null;

    public static Menu currentMenu;

    private float mouseX;
    private float mouseY;

    private Window() {
       this.width = 854;
       this.height = 480;
       this.title = "Krypton Obfuscator";
    }

    public static Window get() {
        if(Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public void run() {
        System.out.println("Started Krypton Obfuscator");

        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // init GLFW
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        // configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // create window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if(glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window!");
        }

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = MemoryStack.stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert vidMode != null;
            glfwSetWindowPos(
                    glfwWindow,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        }

        glfwSetFramebufferSizeCallback(glfwWindow, (window, width, height) -> {
            this.width = width;
            this.height = height;
            glViewport(0, 0, width, height);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, width, height, 0, 1f, -1f);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
        });

        // make the opengl context current
        glfwMakeContextCurrent(glfwWindow);

        // enable v-sync
        glfwSwapInterval(1);

        // make window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        glMatrixMode(GL_PROJECTION);
        glClearDepth(1.0D);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glLoadIdentity();
        glOrtho(0, this.width, this.height, 0, 0, 1);
        glViewport(0, 0, this.width, this.height);

        glfwSetWindowSizeCallback(glfwWindow, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int argWidth, int argHeight) {
                resizeWindow(argWidth, argHeight);
            }
        });

        GLFWUtil.windowID = glfwWindow;

        glfwWindowHint(GLFW_SAMPLES, 4);

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetDropCallback(glfwWindow, FileDropListener::onDrop);

        // init fonts
        Krypton.instance.fontRenderer = new FontManager();

        displayMenu(new ObfuscateMenu());
    }

    public void displayMenu(Menu menu) {
        currentMenu = menu;
        currentMenu.init();
    }

    long lastFrame;

    public void loop() {
        while(!GLFW.glfwWindowShouldClose(glfwWindow)) {
            //calculate delta time
            final long currentTimeMillis = System.currentTimeMillis();
            float delta = (currentTimeMillis - this.lastFrame) / 1000.0f;
            this.lastFrame = currentTimeMillis;

            // poll events
            glfwPollEvents();

            glClearColor(0.15f, 0.15f, 0.15f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            // update width and height of window
            IntBuffer w = BufferUtils.createIntBuffer(1);
            IntBuffer h = BufferUtils.createIntBuffer(1);
            glfwGetWindowSize(glfwWindow, w, h);
            screenWidth = w.get(0);
            screenHeight = h.get(0);

            if(delta >= 0) {
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);

                glfwGetCursorPos(glfwWindow, xBuffer, yBuffer);

                float x = (float) xBuffer.get(0);
                float y = (float) yBuffer.get(0);

                if((x >= 0 && x <= width) && (y >= 0 && y <= height)) {
                    mouseX = x;
                    mouseY = y;
                }

                currentMenu.draw(mouseX, mouseY, delta, glfwWindow, this);
            }

            // Restore window state
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_STENCIL_TEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void resizeWindow(int argWidth, int argHeight) {
        GL11.glViewport(0, 0, argWidth,argHeight);
    }

}
