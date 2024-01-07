package com.ezzenix.window;

import com.ezzenix.Game;
import com.ezzenix.utilities.ImageParser;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private final String title = "Minecraft";
    private int width = 1280;
    private int height = 720;
    private long window; // window handle
    private boolean isMinimized = false;

    //InputStream vertexShaderStream = getClass().getResourceAsStream("/shaders/vertexShader.glsl");
    //InputStream fragmentShaderStream = getClass().getResourceAsStream("/shaders/fragmentShader.glsl");

    public void initialize() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        System.setProperty("org.lwjgl.util.Debug", "true");


        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Detect window size changes
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            Game.getInstance().getRenderer().updateProjection(width, height);
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

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            //if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
            //    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
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
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);

        // Compile shaders
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Load shaders
        String vertexShaderSource;
        try {
            vertexShaderSource = new String(Files.readAllBytes(Paths.get("src/main/resources/shaders/vertexShader.glsl")));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int vertexShader = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
        GL33.glShaderSource(vertexShader, vertexShaderSource);
        GL33.glCompileShader(vertexShader);
        if (GL33.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(GL33.glGetShaderInfoLog(vertexShader));
        }

        String fragmentShaderSource;
        try {
            fragmentShaderSource = new String(Files.readAllBytes(Paths.get("src/main/resources/shaders/fragmentShader.glsl")));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int fragmentShader = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);
        GL33.glShaderSource(fragmentShader, fragmentShaderSource);
        GL33.glCompileShader(fragmentShader);
        if (GL33.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(GL33.glGetShaderInfoLog(fragmentShader));
        }

        // Enable OpenGL debug output
        if (GL.getCapabilities().OpenGL43) {
            glEnable(GL_DEBUG_OUTPUT);
            glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, (int[]) null, true);
            glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
                System.err.println("OpenGL debug message:");
                System.err.println("\tType: " + type);
                System.err.println("\tSeverity: " + severity);
                System.err.println("\tMessage: " + memUTF8(message));
            }, 0);
            System.out.print("OpenGL debug was set up sucessfully");
        } else {
            System.err.println("OpenGL 4.3 or higher is required for debug output.");
        }

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_LIGHTING);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            if (Game.getInstance() != null) {
                Game.getInstance().getInputHandler().handleInput(window);
            }

            if (Game.getInstance() != null) {
                Game.getInstance().getRenderer().render();
            }

            glfwPollEvents();
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

    private void cleanup() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
