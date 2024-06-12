package com.ezzenix.rendering;

import com.ezzenix.Debug;
import com.ezzenix.Game;
import com.ezzenix.engine.gui.Gui;
import com.ezzenix.engine.physics.Raycast;
import com.ezzenix.hud.LineRenderer;
import com.ezzenix.skybox.Skybox;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
	private WorldRenderer worldRenderer;

	Skybox skybox;

	public Renderer() {
		this.worldRenderer = new WorldRenderer();
		glClearColor(110f / 255f, 177f / 255f, 1.0f, 0.0f);

		skybox = new Skybox();

		Hotbar.initialize();
	}

	public void render(long window) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Draw axis lines
		LineRenderer.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 10, 0), new Vector3f(0, 1, 0)); // y
		LineRenderer.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 0, -10), new Vector3f(0, 0, 1)); // z
		LineRenderer.drawLine(new Vector3f(0, 0, 0), new Vector3f(10, 0, 0), new Vector3f(1, 0, 0)); // x

		// Highlight target block
		Raycast result = Game.getInstance().getPlayer().raycast();
		if (result != null) {
			LineRenderer.highlightVoxel(new Vector3f(result.blockPos.x, result.blockPos.y, result.blockPos.z), new Vector3f(0.2f, 0.2f, 0.2f));
		}

		skybox.render();

		worldRenderer.render(window);
		Debug.render();
		LineRenderer.renderBatch();
		Gui.render();

		glfwSwapBuffers(window);
	}

	public WorldRenderer getWorldRenderer() {
		return this.worldRenderer;
	}
}
