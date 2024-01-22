package com.ezzenix;

import com.ezzenix.engine.core.Profiler;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.engine.core.TextureAtlas;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.entities.Player;
import com.ezzenix.game.physics.Physics;
import com.ezzenix.game.world.World;
import com.ezzenix.hud.Hud;
import com.ezzenix.input.InputHandler;
import com.ezzenix.rendering.Camera;
import com.ezzenix.rendering.Renderer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

public class Game {
    private final Window window;

    private final Renderer renderer;
    private final Camera camera;
    private final Player player;
    private final InputHandler inputHandler;
    private final Hud hud;
    private final World world;
    private final List<Entity> entities;

    public final TextureAtlas<String> blockTextures;

    public long TIME_MESH_BUILD = 0;

    public Game() {
        INSTANCE = this;

        // Create a window and initialize OpenGL & glfw
        window = new Window();
        window.setTitle("Minecraft that is better than normal Minecraft");
        window.centerWindow();
        window.setVSync(true);
        window.setIcon("src/main/resources/icon.png");

        // Initialize game
        this.blockTextures = TextureAtlas.fromDirectory("src/main/resources/textures");

        this.entities = new ArrayList<>();
        this.world = new World();
        this.player = new Player(this.world, new Vector3f(0, 100, 0));
        this.camera = new Camera();

        this.hud = new Hud();
        this.renderer = new Renderer();
        this.inputHandler = new InputHandler();

        Scheduler.runPeriodic(() -> {
            //Game.getInstance().getWorld().loadNewChunks();
            //Profiler.dump();
        }, 1000);

        //FrustumBoundingBox test = new FrustumBoundingBox(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

        // Game loop
        while (!window.shouldWindowClose()) {
            Scheduler.update();
            inputHandler.handleInput(window.getId());
            Physics.step();

            this.getRenderer().render(window.getId());

            //boolean isViewingTest = test.isInsideFrustum(camera.getViewProjectionMatrix());
            //System.out.println(isViewingTest);

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
    public Camera getCamera() {
        return this.camera;
    }
    public Player getPlayer() { return this.player; }
    public List<Entity> getEntities() { return this.entities; }


    // Main entry
    public static void main(String[] args) {
        new Game();
    }

    private static Game INSTANCE;
    public static Game getInstance() {
        return INSTANCE;
    }
}