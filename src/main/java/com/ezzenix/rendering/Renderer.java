package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.game.World;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glClearColor;

public class Renderer {
    private WorldRenderer worldRenderer;

    public Renderer() {
        this.worldRenderer = new WorldRenderer();
        glClearColor(110f/255f, 177f/255f, 1.0f, 1.0f);
    }

    public void render(long window) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        worldRenderer.render(window);
        Game.getInstance().getHud().render(window);

        glfwSwapBuffers(window); // swap the color buffers
    }

    public WorldRenderer getWorldRenderer() { return this.worldRenderer; }
}
