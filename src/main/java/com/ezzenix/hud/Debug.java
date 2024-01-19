package com.ezzenix.hud;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.rendering.Camera;
import com.ezzenix.engine.opengl.Mesh;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_LINES;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;

public class Debug {
    private static final Shader debugShader = new Shader("debugLine.vert", "debugLine.frag");

    private static final List<Float> vertexBatch = new ArrayList<>();

    public static void renderBatch() {
        Camera camera = Game.getInstance().getCamera();

        debugShader.use();
        debugShader.uploadMat4f("projectionMatrix", camera.getProjectionMatrix());
        debugShader.uploadMat4f("viewMatrix", camera.getViewMatrix());

        FloatBuffer buffer = Mesh.floatListTobuffer(vertexBatch);
        Mesh mesh = new Mesh(buffer, vertexBatch.size()/6, GL_LINES);
        vertexBatch.clear();

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        mesh.render();
        mesh.dispose();
    }

    public static void drawLine(Vector3f pos1, Vector3f pos2, Vector3f color) {
        vertexBatch.add(pos1.x);
        vertexBatch.add(pos1.y);
        vertexBatch.add(pos1.z);
        vertexBatch.add(color.x);
        vertexBatch.add(color.y);
        vertexBatch.add(color.z);

        vertexBatch.add(pos2.x);
        vertexBatch.add(pos2.y);
        vertexBatch.add(pos2.z);
        vertexBatch.add(color.x);
        vertexBatch.add(color.y);
        vertexBatch.add(color.z);
    }

    public static void drawLine(Vector3f pos1, Vector3f pos2) {
        drawLine(pos1, pos2, new Vector3f(1, 1, 1));
    }

    public static void highlightVoxel(Vector3f voxel) {
        Vector3f vert1 = new Vector3f(0, 0, 0) .add(voxel);
        Vector3f vert2 = new Vector3f(0, 0, 1) .add(voxel);
        Vector3f vert3 = new Vector3f(1, 0, 1) .add(voxel);
        Vector3f vert4 = new Vector3f(1, 0, 0) .add(voxel);
        Vector3f vert5 = new Vector3f(0, 1, 0) .add(voxel);
        Vector3f vert6 = new Vector3f(0, 1, 1) .add(voxel);
        Vector3f vert7 = new Vector3f(1, 1, 1) .add(voxel);
        Vector3f vert8 = new Vector3f(1, 1, 0) .add(voxel);

        drawLine(vert1, vert2);
        drawLine(vert2, vert3);
        drawLine(vert3, vert4);
        drawLine(vert4, vert1);

        drawLine(vert1, vert5);
        drawLine(vert2, vert6);
        drawLine(vert3, vert7);
        drawLine(vert4, vert8);

        drawLine(vert5, vert6);
        drawLine(vert6, vert7);
        drawLine(vert7, vert8);
        drawLine(vert8, vert5);
    }
}
