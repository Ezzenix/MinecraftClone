package com.ezzenix.client.rendering;

import com.ezzenix.Debug;
import com.ezzenix.client.Client;
import com.ezzenix.client.gui.GuiContext;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.physics.Raycast;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
	private static WorldRenderer worldRenderer;
	private static Skybox skybox;

	public static void init() {
		glClearColor(110f / 255f, 177f / 255f, 1.0f, 0.0f);

		worldRenderer = new WorldRenderer();
		skybox = new Skybox();
	}

	public static void render() {
		Window window = Client.getWindow();

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Draw axis lines
		LineRenderer.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 10, 0), new Vector3f(0, 1, 0)); // y
		LineRenderer.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 0, -10), new Vector3f(0, 0, 1)); // z
		LineRenderer.drawLine(new Vector3f(0, 0, 0), new Vector3f(10, 0, 0), new Vector3f(1, 0, 0)); // x

		// Highlight target block
		Raycast result = Client.getPlayer().raycast();
		if (result != null) {
			LineRenderer.highlightVoxel(new Vector3f(result.blockPos.x, result.blockPos.y, result.blockPos.z), new Vector3f(0.2f, 0.2f, 0.2f));
		}

		skybox.render();

		worldRenderer.render(window.getHandle());
		Debug.render();
		LineRenderer.renderBatch();
		//Gui.render();

		Client.getHud().render();

		if (Client.getScreen() != null) {
			Client.getScreen().render();
		}

		GuiContext.renderBatch();

		glfwSwapBuffers(window.getHandle());
	}

	public static WorldRenderer getWorldRenderer() {
		return worldRenderer;
	}
}
