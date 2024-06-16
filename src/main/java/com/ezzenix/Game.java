package com.ezzenix;

import com.ezzenix.client.Client;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.core.TextureAtlas;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.game.worldgen.WorldGeneratorQueue;
import com.ezzenix.client.rendering.Hotbar;
import com.ezzenix.client.rendering.chunkbuilder.ChunkBuilderQueue;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.entities.Player;
import com.ezzenix.engine.physics.Physics;
import com.ezzenix.game.world.World;
import com.ezzenix.client.rendering.Camera;
import com.ezzenix.client.rendering.Renderer;
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
	private final World world;
	private final List<Entity> entities;

	public final TextureAtlas<String> blockTextures;

	public Game() {
		INSTANCE = this;

		// Create a window and initialize OpenGL & glfw
		window = new Window();
		window.setTitle("Minecraft");
		window.centerWindow();
		window.setVSync(false);
		window.setIcon("src/main/resources/icon.png");

		// Initialize
		Input.initialize(window);

		Client.initialize();

		// Initialize game
		this.blockTextures = TextureAtlas.fromDirectory("src/main/resources/textures");

		this.entities = new ArrayList<>();
		this.world = new World();
		this.player = new Player(this.world, new Vector3f(0, 50, 0));
		this.camera = new Camera();

		this.renderer = new Renderer();

		Scheduler.setInterval(() -> {
			Game.getInstance().getWorld().loadNewChunks();
		}, 500);

		// Initialize thread workers
		ChunkBuilderQueue.initialize();
		WorldGeneratorQueue.initialize();

		Hotbar.initialize();

		// Game loop
		while (!window.shouldClose()) {
			Scheduler.update();
			player.updateMovement();
			Physics.step();

			glfwPollEvents();

			this.getRenderer().render(window.getHandle());
			int glError = glGetError();
			if (glError != GL_NO_ERROR) System.err.println("OpenGL Error: " + glError);

			//Scheduler.limitFps(60); // TODO
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