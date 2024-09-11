package com.ezzenix.client;

import com.ezzenix.client.gui.Hud;
import com.ezzenix.client.gui.screen.Screen;
import com.ezzenix.client.gui.widgets.TextFieldWidget;
import com.ezzenix.client.input.Keyboard;
import com.ezzenix.client.input.Mouse;
import com.ezzenix.client.options.GameOptions;
import com.ezzenix.client.rendering.Camera;
import com.ezzenix.client.rendering.Renderer;
import com.ezzenix.client.rendering.chunkbuilder.ChunkBuilderQueue;
import com.ezzenix.client.resource.TextureManager;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.entities.player.Player;
import com.ezzenix.physics.Physics;
import com.ezzenix.world.World;
import com.ezzenix.world.gen.WorldGeneratorQueue;
import com.ezzenix.world.gen.generators.OverworldGenerator;
import org.joml.Vector3f;

import java.io.File;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class Client {

	private static Screen currentScreen;

	private static File gameDirectory;

	private static Window window;
	private static Mouse mouse;
	private static Keyboard keyboard;
	private static Hud hud;

	private static TextureManager textureManager;

	private static GameOptions options;

	private static Camera camera;
	private static Player player;
	private static World world;

	public static TextFieldWidget focusedTextField;

	public static void init() {
		gameDirectory = new File("run");
		if (!gameDirectory.exists()) {
			gameDirectory.mkdir();
		}

		window = new Window(true);
		window.setTitle("Minecraft 1.22");
		window.centerWindow();
		window.setVSync(false);
		window.setIcon("icon.png");

		textureManager = new TextureManager();

		mouse = new Mouse();
		keyboard = new Keyboard();
		hud = new Hud();

		options = new GameOptions();
		options.write();

		world = new World(new OverworldGenerator(1337));
		player = new Player(world, new Vector3f(0, 50, 0));
		camera = new Camera();

		Renderer.init();

		Input.initialize(window);
		ChunkBuilderQueue.initialize();
		WorldGeneratorQueue.initialize();

		Scheduler.setInterval(() -> {
			Client.getWorld().loadNewChunks();
		}, 500);

		// update loop
		while (!window.shouldClose()) {
			update();
		}

		// shutdown
		window.cleanup();
		System.exit(0);
	}

	private static void update() {
		Scheduler.update();
		player.updateMovement();
		Physics.step();

		glfwPollEvents();

		Renderer.render();

		//int glError = glGetError();
		//if (glError != GL_NO_ERROR) System.err.println("OpenGL Error: " + glError);

		int maxFps = Client.getOptions().MAX_FRAME_RATE.getValue();
		if (maxFps != 260) {
			Scheduler.limitFps(maxFps);
		}
	}

	public static void setScreen(Screen screen) {
		if (currentScreen != null) {
			currentScreen.dispose();
			currentScreen.onRemoved();

			if (screen == null && currentScreen.parent != null) { // if closing current and current has a parent, then open the parent
				screen = currentScreen.parent;
			}
		}

		currentScreen = screen;

		if (screen != null) {
			screen.onDisplayed();
			screen.init(getWindow().getWidth(), getWindow().getHeight());
			getMouse().unlockCursor();
		} else {
			getMouse().lockCursor();
		}
	}

	public static Screen getScreen() {
		return currentScreen;
	}
	public static Mouse getMouse() {
		return mouse;
	}
	public static Keyboard getKeyboard() {
		return keyboard;
	}
	public static Hud getHud() {
		return hud;
	}
	public static Window getWindow() {
		return window;
	}
	public static World getWorld() {
		return world;
	}
	public static Camera getCamera() {
		return camera;
	}
	public static Player getPlayer() {
		return player;
	}
	public static GameOptions getOptions() {
		return options;
	}
	public static File getDirectory() {
		return gameDirectory;
	}
	public static TextureManager getTextureManager() {
		return textureManager;
	}

	public static boolean isPaused() {
		if (currentScreen != null) {
			return currentScreen.shouldPauseGame();
		}
		return false;
	}
}
