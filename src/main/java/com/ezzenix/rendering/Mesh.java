package com.ezzenix.rendering;

import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    public final int vao;
    public final int vbo;
    public final int vertexCount;
    private final int primitive;

    public Mesh(FloatBuffer buffer, int vertexCount, int primitive) {
        this.primitive = primitive;
        this.vertexCount = vertexCount;

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public Mesh(FloatBuffer buffer, int vertexCount) {
        this(buffer, vertexCount, GL_TRIANGLES);
    }

    public void render() {
        glBindVertexArray(this.vao);
        glDrawArrays(primitive, 0, this.vertexCount);
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