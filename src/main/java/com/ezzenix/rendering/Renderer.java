package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.hud.Debug;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private WorldRenderer worldRenderer;

    public Renderer() {
        this.worldRenderer = new WorldRenderer();
        glClearColor(110f / 255f, 177f / 255f, 1.0f, 1.0f);
    }

    public void render(long window) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Debug.drawLine(new Vector3f(0, -1, 0), new Vector3f(0, 9, 0), new Vector3f(0, 1, 0)); // y
        Debug.drawLine(new Vector3f(0, -1, 0), new Vector3f(0, -1, -10), new Vector3f(0, 0, 1)); // z
        Debug.drawLine(new Vector3f(0, -1, 0), new Vector3f(10, -1, 0), new Vector3f(1, 0, 0)); // x

        worldRenderer.render(window);
        Game.getInstance().getHud().render(window);
        Debug.renderBatch();

        glfwSwapBuffers(window); // swap the color buffers
    }

    public WorldRenderer getWorldRenderer() {
        return this.worldRenderer;
    }
}
