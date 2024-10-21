package com.ezzenix.rendering;

import com.ezzenix.Client;
import com.ezzenix.Debug;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.gui.Color;
import com.ezzenix.physics.Raycast;
import com.ezzenix.rendering.particle.ParticleSystem;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
	private static WorldRenderer worldRenderer;
	private static Skybox skybox;

	public static void init() {
		glClearColor(110f / 255f, 177f / 255f, 1.0f, 0.0f);

		worldRenderer = new WorldRenderer();
		skybox = new Skybox();

		//new Particle(new Vector3f(0, 50, 0)).setColor(Color.packColor(255, 0, 0, 255)).setSize(0.1f).setVelocity(new Vector3f(0, 1, 0));
	}

	public static void render() {
		Window window = Client.getWindow();

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Draw axis lines
		LineRenderer.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 10, 0), Color.pack(0f, 1f, 0f, 1f)); // y
		LineRenderer.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 0, -10), Color.pack(0f, 0f, 1f, 1f)); // z
		LineRenderer.drawLine(new Vector3f(0, 0, 0), new Vector3f(10, 0, 0), Color.pack(1f, 0f, 0f, 1f)); // x

		// Highlight target block
		Raycast result = Client.getPlayer().raycast();
		if (result != null) {
			LineRenderer.highlightVoxel(new Vector3f(result.blockPos.x, result.blockPos.y, result.blockPos.z), Color.pack(0.2f, 0.2f, 0.2f, 1f));
		}

		skybox.render();

		worldRenderer.render();
		Debug.render();
		LineRenderer.renderBatch();

		ParticleSystem.render();

		Client.getHud().render();

		if (Client.getScreen() != null) {
			Client.getScreen().render();
		}

		glfwSwapBuffers(window.getHandle());
	}

	public static WorldRenderer getWorldRenderer() {
		return worldRenderer;
	}
}
