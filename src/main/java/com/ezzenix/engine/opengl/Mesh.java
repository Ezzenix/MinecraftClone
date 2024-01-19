package com.ezzenix.engine.opengl;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.BufferUtils.createFloatBuffer;
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

        if (this.vao == -1 || this.vbo == -1) {
            System.err.println("Mesh failed to generate VAO or VBO");
        }
    }

    public Mesh(FloatBuffer buffer, int vertexCount) {
        this(buffer, vertexCount, GL_TRIANGLES);
    }

    public void render() {
        if (vertexCount == 0) return;
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBindVertexArray(this.vao);
        glDrawArrays(primitive, 0, this.vertexCount);
        glBindVertexArray(0);
    }

    public void dispose() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }

    // Call this after adding all the attributes
    public void unbind() {
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    // Helper methods
    public static FloatBuffer floatArrayToBuffer(float[] floats) {
        FloatBuffer buffer = createFloatBuffer(floats.length);
        buffer.put(floats);
        buffer.flip();
        return buffer;
    }

    public static FloatBuffer floatListTobuffer(List<Float> floats) {
        float[] floatArray = new float[floats.size()];
        for (int i = 0; i < floats.size(); i++) {
            floatArray[i] = floats.get(i);
        }
        return floatArrayToBuffer(floatArray);
    }
}