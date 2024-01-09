package com.ezzenix.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Mesh {
    public int vao;
    public int vbo;
    public int vertexCount;

    public Mesh(FloatBuffer buffer, int vertexCount) {
        this.vertexCount = vertexCount;
        try (MemoryStack stack = stackPush()) {
            this.vao = glGenVertexArrays();
            glBindVertexArray(this.vao);

            this.vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }

        glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
    }

    public void render() {
        glBindVertexArray(this.vao);
        glDrawArrays(GL_TRIANGLES, 0, this.vertexCount);
        glBindVertexArray(0);
    }

    public void destroy() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }

    // Call this after adding all the attributes
    public void unbind() {
        glBindVertexArray(0);
        glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
}