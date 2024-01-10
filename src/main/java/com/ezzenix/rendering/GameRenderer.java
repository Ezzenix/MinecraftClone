package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.game.Chunk;
import com.ezzenix.game.World;
import com.ezzenix.utils.ImageUtil;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GameRenderer {

    private final Camera camera;

    private final int worldShader;

    public GameRenderer() {
        camera = new Camera();

        this.worldShader = Shader.makeProgram("world.vert", "world.frag");
        if (worldShader == -1) {
            System.err.println("World shader program failed to load!");
            System.exit(-1);
        }
    }

    public Camera getCamera() {
        return this.camera;
    }

    private static int blockTexture = ImageUtil.loadTexture(Game.getInstance().blockTextures.getAtlasImage());

    public void render(long window) {
        World world = Game.getInstance().getWorld();

        glBindTexture(GL_TEXTURE_2D, blockTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        if (world != null) {
            glUseProgram(worldShader);

            int projectionMatrixLocation = glGetUniformLocation(worldShader, "projectionMatrix");
            glUniformMatrix4fv(projectionMatrixLocation, false, Game.getInstance().getRenderer().getCamera().getProjectionMatrix().get(new float[16]));
            int viewMatrixLocation = glGetUniformLocation(worldShader, "viewMatrix");
            glUniformMatrix4fv(viewMatrixLocation, false, Game.getInstance().getRenderer().getCamera().getViewMatrix().get(new float[16]));

            for (Chunk chunk : world.getChunks().values()) {
                Mesh mesh = chunk.getMesh();
                if (mesh != null) {
                    mesh.render();
                }
            }
        }
    }
}
