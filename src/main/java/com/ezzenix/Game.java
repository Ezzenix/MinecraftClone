package com.ezzenix;

import com.ezzenix.engine.opengl.GLWindow;
import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.engine.utils.TextureAtlas;
import com.ezzenix.game.World;
import com.ezzenix.hud.Hud;
import com.ezzenix.rendering.GameRenderer;
import com.ezzenix.window.InputHandler;

public class Game {
    private final GLWindow window;

    private final GameRenderer gameRenderer;
    private final InputHandler inputHandler;
    private final Hud hud;
    private final World world;

    public final TextureAtlas<String> blockTextures;

    public Game() {
        INSTANCE = this;

        // Create a window and initialize OpenGL & glfw
        window = new GLWindow();
        window.setTitle("Minecraft");
        window.centerWindow();
        window.setVSync(true);
        window.setIcon("src/main/resources/icon.png");

        // Initialize game
        this.blockTextures = TextureAtlas.fromDirectory("src/main/resources/textures");
        this.hud = new Hud();
        this.gameRenderer = new GameRenderer();
        this.inputHandler = new InputHandler();
        this.world = new World();

        // Game loop
        while (!window.shouldWindowClose()) {
            Scheduler.update();
            inputHandler.handleInput(window.getId());
        }

        // Shutdown
        window.cleanup();
    }


    // Getters
    public GLWindow getWindow() {
        return this.window;
    }
    public GameRenderer getRenderer() {
        return this.gameRenderer;
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


    // Main entry
    public static void main(String[] args) {
        new Game();
    }

    private static Game INSTANCE;
    public static Game getInstance() {
        return INSTANCE;
    }
}