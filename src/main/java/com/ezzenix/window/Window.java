package com.ezzenix.window;

import com.ezzenix.Game;
import com.ezzenix.rendering.Camera;
import com.ezzenix.rendering.Shader;
import com.ezzenix.utilities.ImageParser;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.text.DecimalFormat;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
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

        Configuration.DEBUG.set(true);
        Configuration.DEBUG_FUNCTIONS.set(true);
        Configuration.DEBUG_LOADER.set(true);
        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
        Configuration.DEBUG_MEMORY_ALLOCATOR_FAST.set(true);
        Configuration.DEBUG_STACK.set(true);

        if ( !glfwInit() )
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
        if ( window == NULL )
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

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            //if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
            //    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (key == GLFW_KEY_H) {
                Camera camera = Game.getInstance().getRenderer().getCamera();
                System.out.println("LookVector " + camera.getLookVector().toString(new DecimalFormat("#.###")));
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

    private void loop() {
        int shaderProgram = Shader.makeProgram("vertexShader.glsl", "fragmentShader.glsl");
        if (shaderProgram == -1) {
            System.err.println("Shader program failed to load!");
            System.exit(-1);
        }

        int vao;
        int vbo;
        try (MemoryStack stack = stackPush()) {
            FloatBuffer buffer = stackMallocFloat(3 * 24);
            // Front face
            buffer.put(-0.5f).put(-0.5f).put(0.5f);   // Vertex 1 (front-bottom-left)
            buffer.put(0.5f).put(-0.5f).put(0.5f);    // Vertex 2 (front-bottom-right)
            buffer.put(0.5f).put(0.5f).put(0.5f);     // Vertex 3 (front-top-right)
            buffer.put(-0.5f).put(0.5f).put(0.5f);    // Vertex 4 (front-top-left)

// Back face
            buffer.put(-0.5f).put(-0.5f).put(-0.5f);  // Vertex 5 (back-bottom-left)
            buffer.put(0.5f).put(-0.5f).put(-0.5f);   // Vertex 6 (back-bottom-right)
            buffer.put(0.5f).put(0.5f).put(-0.5f);    // Vertex 7 (back-top-right)
            buffer.put(-0.5f).put(0.5f).put(-0.5f);   // Vertex 8 (back-top-left)

// Left face
            buffer.put(-0.5f).put(-0.5f).put(-0.5f);  // Vertex 9 (left-bottom-back)
            buffer.put(-0.5f).put(-0.5f).put(0.5f);   // Vertex 10 (left-bottom-front)
            buffer.put(-0.5f).put(0.5f).put(0.5f);    // Vertex 11 (left-top-front)
            buffer.put(-0.5f).put(0.5f).put(-0.5f);   // Vertex 12 (left-top-back)

// Right face
            buffer.put(0.5f).put(-0.5f).put(-0.5f);   // Vertex 13 (right-bottom-back)
            buffer.put(0.5f).put(-0.5f).put(0.5f);    // Vertex 14 (right-bottom-front)
            buffer.put(0.5f).put(0.5f).put(0.5f);     // Vertex 15 (right-top-front)
            buffer.put(0.5f).put(0.5f).put(-0.5f);    // Vertex 16 (right-top-back)

// Top face
            buffer.put(-0.5f).put(0.5f).put(0.5f);    // Vertex 17 (top-front-left)
            buffer.put(0.5f).put(0.5f).put(0.5f);     // Vertex 18 (top-front-right)
            buffer.put(0.5f).put(0.5f).put(-0.5f);    // Vertex 19 (top-back-right)
            buffer.put(-0.5f).put(0.5f).put(-0.5f);   // Vertex 20 (top-back-left)

// Bottom face
            buffer.put(-0.5f).put(-0.5f).put(0.5f);   // Vertex 21 (bottom-front-left)
            buffer.put(0.5f).put(-0.5f).put(0.5f);    // Vertex 22 (bottom-front-right)
            buffer.put(0.5f).put(-0.5f).put(-0.5f);   // Vertex 23 (bottom-back-right)
            buffer.put(-0.5f).put(-0.5f).put(-0.5f);  // Vertex 24 (bottom-back-left)
            buffer.flip();

            vao = glGenVertexArrays();
            glBindVertexArray(vao);

            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }

        glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);
        glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        glUseProgram(shaderProgram);



        // Set defaults
        glClearColor(1.0f, 0.4f, 0.4f, 0.0f);
        //glEnable(GL_DEPTH_TEST);
        //glEnable(GL_LIGHTING);

        while (!glfwWindowShouldClose(window)) {
            if (Game.getInstance() != null) {
                Game.getInstance().getInputHandler().handleInput(window);
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glUseProgram(shaderProgram);

            int projectionMatrixLocation = glGetUniformLocation(shaderProgram, "projectionMatrix");
            glUniformMatrix4fv(projectionMatrixLocation, false, Game.getInstance().getRenderer().getCamera().getProjectionMatrix().get(new float[16]));
            int viewMatrixLocation = glGetUniformLocation(shaderProgram, "viewMatrix");
            glUniformMatrix4fv(viewMatrixLocation, false, Game.getInstance().getRenderer().getCamera().getViewMatrix().get(new float[16]));

            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 24);
            glBindVertexArray(0);

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

    private void cleanup() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
