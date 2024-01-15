package com.ezzenix.hud;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.rendering.Camera;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class DebugLines {
    private static final Shader debugShader = new Shader("debugLine.vert", "debugLine.frag");

    public static void draw(Vector3f pos1, Vector3f pos2, Vector3f color) {
        Camera camera = Game.getInstance().getCamera();

        debugShader.use();
        debugShader.uploadMat4f("projectionMatrix", camera.getProjectionMatrix());
        debugShader.uploadMat4f("viewMatrix", camera.getViewMatrix());
        debugShader.uploadVec3f("lineColor", color);

        // Create vertices for the line
        float[] vertices = {
                pos1.x(), pos1.y(), pos1.z(),
                pos2.x(), pos2.y(), pos2.z()
        };

        // Create VAO and VBO for the line
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Specify vertex attribute pointers
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Bind the VAO and draw the line
        glBindVertexArray(vao);
        glDrawArrays(GL_LINES, 0, vertices.length / 3);
        glBindVertexArray(0);

        // Unbind VAO and VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        // Clean up VAO and VBO
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}
