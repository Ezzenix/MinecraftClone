package com.ezzenix.window;

import com.ezzenix.Game;
import com.ezzenix.game.Chunk;
import com.ezzenix.game.World;
import com.ezzenix.rendering.Camera;
import com.ezzenix.rendering.Mesh;
import com.ezzenix.rendering.Shader;
import com.ezzenix.utils.ImageParser;
import com.ezzenix.utils.ImageUtil;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class Window {
    private final String title = "Minecraft";
    private int width = 1280;
    private int height = 720;
    private long window; // window handle
    private boolean isMinimized = false;

    //InputStream vertexShaderStream = getClass().getResourceAsStream("/shaders/vertexShader.glsl");
    //InputStream fragmentShaderStream = getClass().getResourceAsStream("/shaders/fragmentShader.glsl");


    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        System.setProperty("org.lwjgl.util.Debug", "true");

        Configuration.DEBUG.set(true);
        Configuration.DEBUG_FUNCTIONS.set(true);
        Configuration.DEBUG_LOADER.set(true);
        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
        Configuration.DEBUG_MEMORY_ALLOCATOR_FAST.set(true);
        Configuration.DEBUG_STACK.set(true);

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
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
            if (key == GLFW_KEY_H) {
                Camera camera = Game.getInstance().getRenderer().getCamera();
                System.out.println("LookVector " + camera.getLookVector().toString(new DecimalFormat("#.###")));
            }

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

        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
    }

    public void loop() {
        int shaderProgram = Shader.makeProgram("vertexShader.glsl", "fragmentShader.glsl");
        if (shaderProgram == -1) {
            System.err.println("Shader program failed to load!");
            System.exit(-1);
        }

        glUseProgram(shaderProgram);


        // Set defaults
        glClearColor(0.20f, 0.72f, 0.92f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        int blockTexture = ImageUtil.loadTexture(Game.getInstance().blockTextures.getAtlasImage());

        World world = Game.getInstance().getWorld();

        long lastFrame = System.currentTimeMillis();
        while (!glfwWindowShouldClose(window)) {
            Game.getInstance().deltaTime = (float) (System.currentTimeMillis() - lastFrame);
            lastFrame = System.currentTimeMillis();

            if (Game.getInstance() != null) {
                Game.getInstance().getInputHandler().handleInput(window);
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glBindTexture(GL_TEXTURE_2D, blockTexture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            if (world != null) {
                glUseProgram(shaderProgram);

                int projectionMatrixLocation = glGetUniformLocation(shaderProgram, "projectionMatrix");
                glUniformMatrix4fv(projectionMatrixLocation, false, Game.getInstance().getRenderer().getCamera().getProjectionMatrix().get(new float[16]));
                int viewMatrixLocation = glGetUniformLocation(shaderProgram, "viewMatrix");
                glUniformMatrix4fv(viewMatrixLocation, false, Game.getInstance().getRenderer().getCamera().getViewMatrix().get(new float[16]));

                for (Chunk chunk : world.getChunks().values()) {
                    Mesh mesh = chunk.getMesh();
                    if (mesh != null) {
                        mesh.render();
                    }
                }
            }

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
