package com.ezzenix;

import com.ezzenix.engine.Input;
import com.ezzenix.engine.core.TextureAtlas;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.game.worldgen.WorldGeneratorQueue;
import com.ezzenix.rendering.chunkbuilder.ChunkBuilderQueue;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.entities.Player;
import com.ezzenix.engine.physics.Physics;
import com.ezzenix.game.world.World;
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
	private final World world;
	private final List<Entity> entities;

	public final TextureAtlas<String> blockTextures;

	public Game() {
		INSTANCE = this;

		// Create a window and initialize OpenGL & glfw
		window = new Window();
		window.setTitle("Minecraft");
		window.centerWindow();
		window.setVSync(true);
		window.setIcon("src/main/resources/icon.png");

		// Initialize
		Input.initialize(window);

		// Initialize game
		this.blockTextures = TextureAtlas.fromDirectory("src/main/resources/textures");

		this.entities = new ArrayList<>();
		this.world = new World();
		this.player = new Player(this.world, new Vector3f(0, 50, 0));
		this.camera = new Camera();

		this.renderer = new Renderer();
		this.inputHandler = new InputHandler();

		Scheduler.setInterval(() -> {
			Game.getInstance().getWorld().loadNewChunks();
		}, 500);

		// Initialize thread workers
		ChunkBuilderQueue.initialize();
		WorldGeneratorQueue.initialize();

		// Game loop
		while (!window.shouldWindowClose()) {
			Scheduler.update();
			inputHandler.handleInput(window.getId());
			Physics.step();

			this.getRenderer().render(window.getId());

			glfwPollEvents();
			int glError = glGetError();
			if (glError != GL_NO_ERROR) System.err.println("OpenGL Error: " + glError);
		}

		// Shutdown
		window.cleanup();
		System.exit(0);
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
	public Camera getCamera() {
		return this.camera;
	}
	public Player getPlayer() {
		return this.player;
	}
	public List<Entity> getEntities() {
		return this.entities;
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