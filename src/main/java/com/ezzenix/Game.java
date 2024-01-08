package com.ezzenix;

import com.ezzenix.rendering.GameRenderer;
import com.ezzenix.utilities.TextureAtlas;
import com.ezzenix.window.InputHandler;
import com.ezzenix.window.Window;
import org.joml.Vector2f;
import org.lwjgl.Version;

import java.util.List;

public class Game {
    private final GameRenderer gameRenderer;
    private final Window window;
    private final InputHandler inputHandler;

    public Game() {
        INSTANCE = this;

        TextureAtlas atlas = new TextureAtlas("src/main/resources/textures");
        //List<Vector2f> uvs = atlas.getTextureUVs("oak_planks");
        //System.out.println(uvs);

        this.gameRenderer = new GameRenderer();
        this.window = new Window();
        this.inputHandler = new InputHandler();

        this.window.initialize();
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