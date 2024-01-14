package com.ezzenix.engine.opengl.utils;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Face {
    public static final Vector3f TOP = new Vector3f(0, 1, 0);
    public static final Vector3f BOTTOM = new Vector3f(0, -1, 0);
    public static final Vector3f BACK = new Vector3f(0, 0, -1);
    public static final Vector3f FRONT = new Vector3f(0, 0, 1);
    public static final Vector3f LEFT = new Vector3f(-1, 0, 0);
    public static final Vector3f RIGHT = new Vector3f(1, 0, 0);

    public static final List<Vector3f> ALL = new ArrayList<>();

    static {
        ALL.add(com.ezzenix.engine.opengl.utils.Face.TOP);
        ALL.add(com.ezzenix.engine.opengl.utils.Face.BACK);
        ALL.add(com.ezzenix.engine.opengl.utils.Face.BOTTOM);
        ALL.add(com.ezzenix.engine.opengl.utils.Face.RIGHT);
        ALL.add(com.ezzenix.engine.opengl.utils.Face.LEFT);
        ALL.add(com.ezzenix.engine.opengl.utils.Face.FRONT);
    }

    public static List<Vector3f> faceUnitCube(Vector3f face) {
        List<Vector3f> list = new ArrayList<>();

        if (face.equals(com.ezzenix.engine.opengl.utils.Face.TOP)) {
            // Top face
            list.add(new Vector3f(-0.5f, 0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, 0.5f, 0.5f));
            list.add(new Vector3f(0.5f, 0.5f, 0.5f));
            list.add(new Vector3f(0.5f, 0.5f, -0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.Face.BOTTOM)) {
            // Bottom face
            list.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(0.5f, -0.5f, 0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.Face.FRONT)) {
            // Front face
            list.add(new Vector3f(-0.5f, 0.5f, 0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(0.5f, 0.5f, 0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.Face.BACK)) {
            // Back face
            list.add(new Vector3f(0.5f, 0.5f, -0.5f));
            list.add(new Vector3f(0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, 0.5f, -0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.Face.LEFT)) {
            // Left face
            list.add(new Vector3f(-0.5f, 0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(-0.5f, 0.5f, 0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.Face.RIGHT)) {
            // Right face
            list.add(new Vector3f(0.5f, 0.5f, 0.5f));
            list.add(new Vector3f(0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(0.5f, 0.5f, -0.5f));
        }

        return list;
    }
}
