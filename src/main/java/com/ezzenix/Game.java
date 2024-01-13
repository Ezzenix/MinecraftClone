package com.ezzenix;

import com.ezzenix.engine.utils.TextureAtlas;
import com.ezzenix.game.World;
import com.ezzenix.hud.Hud;
import com.ezzenix.rendering.GameRenderer;
import com.ezzenix.window.InputHandler;
import com.ezzenix.window.Window;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Game {
    private final GameRenderer gameRenderer;
    private final Window window;
    private final InputHandler inputHandler;
    private final Hud hud;
    private final World world;

    public final TextureAtlas<String> blockTextures;

    public float deltaTime = (float) 1 / 60;
    public float fps = 0;

    public Game() {
        INSTANCE = this;

        this.window = new Window();
        this.window.init();

        this.blockTextures = TextureAtlas.fromDirectory("src/main/resources/textures");

        try {
            ImageIO.write(this.blockTextures.getAtlasImage(), "PNG", new File("blockTextures.png"));
        } catch (IOException ignore) {}

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