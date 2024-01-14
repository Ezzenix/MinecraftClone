package com.ezzenix.engine.opengl;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLWindow {
    private String title = "New window";
    private int width = 1280;
    private int height = 720;
    private long window;
    private boolean isMinimized = true;

    private Runnable init;
    private Runnable gameLoop;
    private Runnable cleanup;

    public GLWindow() {
        this.initWindow();
    }

    private void initWindow() {
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
            glViewport(0, 0, width, height);
        });

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
        });

        glfwSetWindowIconifyCallback(window, (window, iconified) -> {
            this.isMinimized = iconified;
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        createCapabilities();
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(window, title);
        this.title = title;
    }

    public void centerWindow() {
        GLFWVidMode monitorResolution = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (monitorResolution == null) return;
        glfwSetWindowPos(window, (monitorResolution.width() - width) / 2, (monitorResolution.height() - height) / 2);
    }

    public void setVSync(boolean enabled) {
        glfwSwapInterval(enabled ? 1 : 0);
    }

    public void showWindow() {
        glfwShowWindow(window);
    }

    public void setIcon(String path) {
        GLFWImage image = GLFWImage.malloc();
        GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1);
        com.ezzenix.engine.opengl.utils.ImageParser resource = com.ezzenix.engine.opengl.utils.ImageParser.loadImage(path);
        if (resource != null) {
            image.set(resource.getWidth(), resource.getHeight(), resource.getImage());
            imageBuffer.put(0, image);
            glfwSetWindowIcon(window, imageBuffer);
        } else {
            System.out.println("Icon failed to load.");
        }
    }

    public long getId() {
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

    public boolean shouldWindowClose() {
        return glfwWindowShouldClose(window);
    }
}
