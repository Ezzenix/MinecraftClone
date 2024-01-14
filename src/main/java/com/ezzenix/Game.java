package com.ezzenix;

import com.ezzenix.engine.opengl.Window;
import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.engine.utils.TextureAtlas;
import com.ezzenix.game.World;
import com.ezzenix.hud.Hud;
import com.ezzenix.rendering.Camera;
import com.ezzenix.rendering.WorldRenderer;
import com.ezzenix.rendering.Renderer;
import com.ezzenix.window.InputHandler;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

public class Game {
    private final Window window;

    private final Renderer renderer;
    private final Camera camera;
    private final InputHandler inputHandler;
    private final Hud hud;
    private final World world;

    public final TextureAtlas<String> blockTextures;

    public Game() {
        INSTANCE = this;

        // Create a window and initialize OpenGL & glfw
        window = new Window();
        window.setTitle("Minecraft");
        window.centerWindow();
        window.setVSync(true);
        window.setIcon("src/main/resources/icon.png");

        // Initialize game
        this.blockTextures = TextureAtlas.fromDirectory("src/main/resources/textures");
        this.camera = new Camera();
        this.world = new World();
        this.hud = new Hud();
        this.renderer = new Renderer();
        this.inputHandler = new InputHandler();

        Scheduler.runPeriodic(() -> {
            Game.getInstance().getWorld().loadNewChunks();
        }, 1000);

        // Game loop
        while (!window.shouldWindowClose()) {
            Scheduler.update();
            inputHandler.handleInput(window.getId());

            this.getRenderer().render(window.getId());

            glfwPollEvents();
            int glError = glGetError();
            if (glError != GL_NO_ERROR) System.err.println("OpenGL Error: " + glError);

        }

        // Shutdown
        window.cleanup();
    }


    // Getters
    public Window getWindow() {
        return this.window;
    }
    public Renderer getRenderer() {
        return this.renderer;
    }
    public InputHandler getInputHandler() {
        return this.inputHandler;
    }
    public World getWorld() {
        return this.world;
    }
    public Hud getHud() {
        return this.hud;
    }
    public Camera getCamera() { return this.camera; }


    // Main entry
    public static void main(String[] args) {
        new Game();
    }

    private static Game INSTANCE;
    public static Game getInstance() {
        return INSTANCE;
    }
}