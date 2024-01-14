package com.ezzenix.engine.opengl.utils;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class OldFace {
    public static final Vector3f TOP = new Vector3f(0, 1, 0);
    public static final Vector3f BOTTOM = new Vector3f(0, -1, 0);
    public static final Vector3f BACK = new Vector3f(0, 0, -1);
    public static final Vector3f FRONT = new Vector3f(0, 0, 1);
    public static final Vector3f LEFT = new Vector3f(-1, 0, 0);
    public static final Vector3f RIGHT = new Vector3f(1, 0, 0);

    public static final List<Vector3f> ALL = new ArrayList<>();

    static {
        ALL.add(com.ezzenix.engine.opengl.utils.OldFace.TOP);
        ALL.add(com.ezzenix.engine.opengl.utils.OldFace.BACK);
        ALL.add(com.ezzenix.engine.opengl.utils.OldFace.BOTTOM);
        ALL.add(com.ezzenix.engine.opengl.utils.OldFace.RIGHT);
        ALL.add(com.ezzenix.engine.opengl.utils.OldFace.LEFT);
        ALL.add(com.ezzenix.engine.opengl.utils.OldFace.FRONT);
    }

    public static List<Vector3f> faceUnitCube(Vector3f face) {
        List<Vector3f> list = new ArrayList<>();

        if (face.equals(com.ezzenix.engine.opengl.utils.OldFace.TOP)) {
            // Top face
            list.add(new Vector3f(-0.5f, 0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, 0.5f, 0.5f));
            list.add(new Vector3f(0.5f, 0.5f, 0.5f));
            list.add(new Vector3f(0.5f, 0.5f, -0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.OldFace.BOTTOM)) {
            // Bottom face
            list.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(0.5f, -0.5f, 0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.OldFace.FRONT)) {
            // Front face
            list.add(new Vector3f(-0.5f, 0.5f, 0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(0.5f, 0.5f, 0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.OldFace.BACK)) {
            // Back face
            list.add(new Vector3f(0.5f, 0.5f, -0.5f));
            list.add(new Vector3f(0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, 0.5f, -0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.OldFace.LEFT)) {
            // Left face
            list.add(new Vector3f(-0.5f, 0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(-0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(-0.5f, 0.5f, 0.5f));
        } else if (face.equals(com.ezzenix.engine.opengl.utils.OldFace.RIGHT)) {
            // Right face
            list.add(new Vector3f(0.5f, 0.5f, 0.5f));
            list.add(new Vector3f(0.5f, -0.5f, 0.5f));
            list.add(new Vector3f(0.5f, -0.5f, -0.5f));
            list.add(new Vector3f(0.5f, 0.5f, -0.5f));
        }

        return list;
    }
}
