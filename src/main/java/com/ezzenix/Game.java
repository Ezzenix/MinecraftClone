package com.ezzenix;

import com.ezzenix.game.World;
import com.ezzenix.rendering.GameRenderer;
import com.ezzenix.utilities.Face;
import com.ezzenix.utilities.TextureAtlas;
import com.ezzenix.window.InputHandler;
import com.ezzenix.window.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.system.Configuration;

import java.text.DecimalFormat;
import java.util.List;

public class Game {
    private final GameRenderer gameRenderer;
    private final Window window;
    private final InputHandler inputHandler;
    private World world;

    public TextureAtlas blockTextures;

    public Game() {
        INSTANCE = this;

        this.window = new Window();
        this.window.init();

        this.blockTextures = new TextureAtlas("src/main/resources/textures");

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