package com.ezzenix;

import com.ezzenix.game.World;
import com.ezzenix.hud.Hud;
import com.ezzenix.rendering.GameRenderer;
import com.ezzenix.utils.textures.TextureAtlas;
import com.ezzenix.window.InputHandler;
import com.ezzenix.window.Window;
import org.lwjgl.Version;

public class Game {
    private final GameRenderer gameRenderer;
    private final Window window;
    private final InputHandler inputHandler;
    private Hud hud;
    private World world;

    public TextureAtlas blockTextures;

    public float deltaTime = (float) 1 /60;

    public Game() {
        INSTANCE = this;

        this.window = new Window();
        this.window.init();

        this.blockTextures = new TextureAtlas("src/main/resources/textures");

        this.hud = new Hud();
        this.gameRenderer = new GameRenderer();
        this.inputHandler = new InputHandler();
        this.world = new World();

        this.window.loop();
        this.window.cleanup();
    }

    public Window getWindow() {
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
    public Hud getHud() { return this.hud; }


    // Main entry
    public static void main(String[] args) {
        System.out.println("LWJGL version: " + Version.getVersion());
        new Game();
    }

    private static Game INSTANCE;

    public static Game getInstance() {
        return INSTANCE;
    }
}