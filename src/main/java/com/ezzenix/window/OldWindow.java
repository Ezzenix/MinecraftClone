package com.ezzenix.window;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.utils.ImageParser;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OldWindow {
    private final String title = "Minecraft";
    private int width = 1280;
    private int height = 720;
    private long window; // window handle
    private boolean isMinimized = false;

    //InputStream vertexShaderStream = getClass().getResourceAsStream("/shaders/world.vert");
    //InputStream fragmentShaderStream = getClass().getResourceAsStream("/shaders/world.frag");


    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        //System.setProperty("org.lwjgl.util.Debug", "true");

        /*
        Configuration.DEBUG.set(true);
        Configuration.DEBUG_FUNCTIONS.set(true);
        Configuration.DEBUG_LOADER.set(true);
        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
        Configuration.DEBUG_MEMORY_ALLOCATOR_FAST.set(true);
        Configuration.DEBUG_STACK.set(true);
        */

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Detect window size changes
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            //Game.getInstance().getRenderer().updateProjection(width, height);
            glViewport(0, 0, width, height);
        });

        // Icon
        GLFWImage image = GLFWImage.malloc();
        GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1);
        ImageParser resource = ImageParser.loadImage("src/main/resources/icon.png");
        if (resource != null) {
            image.set(resource.getWidth(), resource.getHeight(), resource.getImage());
            imageBuffer.put(0, image);
            glfwSetWindowIcon(window, imageBuffer);
        } else {
            System.out.println("Icon failed to load.");
        }

        AtomicBoolean wireframeMode = new AtomicBoolean(false);
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_Z && action == GLFW_RELEASE) {
                wireframeMode.set(!wireframeMode.get());
                if (wireframeMode.get()) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    glDisable(GL_DEPTH_TEST);
                    glDisable(GL_CULL_FACE);
                } else {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    glEnable(GL_DEPTH_TEST);
                    glEnable(GL_CULL_FACE);
                }
            }

            if (key == GLFW_KEY_G && action == GLFW_RELEASE) {
                MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
                System.out.println("Memory: " + heapMemoryUsage.getUsed() / (1024 * 1024) + "/" + heapMemoryUsage.getMax() / (1024 * 1024) + " MB");
            }
        });

        glfwSetWindowIconifyCallback(window, (window, iconified) -> {
            this.isMinimized = iconified;
        });

        // Center the window
        GLFWVidMode monitorResolution = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                window,
                (monitorResolution.width() - width) / 2,
                (monitorResolution.height() - height) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        createCapabilities();
        glfwSwapInterval(1); // v-sync
        glfwShowWindow(window);

        // Enable OpenGL debug output
        /*
        if (GL.getCapabilities().OpenGL43) {
            glEnable(GL_DEBUG_OUTPUT);
            glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, (int[]) null, true);
            glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
                System.err.println("\nOpenGL debug message:");
                System.err.println("\tType: " + type);
                System.err.println("\tSeverity: " + severity);
                System.err.println("\tMessage: " + memUTF8(message));
            }, 0);
            System.out.println("OpenGL debug was set up sucessfully");
        } else {
            System.err.println("OpenGL 4.3 or higher is required for debug output.");
        }
        */

        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
    }

    public void loop() {
        // Set defaults
        glClearColor(0.20f, 0.72f, 0.92f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);

        long lastChunkLoad = 0;
        long lastFrame = System.currentTimeMillis();
        while (!glfwWindowShouldClose(window)) {
            long deltaTime = (System.currentTimeMillis() - lastFrame);
            //Game.getInstance().deltaTime = (float) deltaTime;
            //Game.getInstance().fps = (float)Math.round(1000f / (float)deltaTime);
            lastFrame = System.currentTimeMillis();
            if (System.currentTimeMillis() > (lastChunkLoad + 1000)) {
                lastChunkLoad = System.currentTimeMillis();
                Game.getInstance().getWorld().loadNewChunks();
            }

            if (Game.getInstance() != null) {
                Game.getInstance().getInputHandler().handleInput(window);
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Game.getInstance().getRenderer().render(window);
            Game.getInstance().getHud().render(window);

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();

            int error = glGetError();
            if (error != GL_NO_ERROR) {
                System.err.println("OpenGL Error: " + error);
            }
        }
    }

    public int getWindowWidth() {
        return width;
    }

    public int getWindowHeight() {
        return height;
    }

    public long getHandle() {
        return window;
    }

    public void cleanup() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
