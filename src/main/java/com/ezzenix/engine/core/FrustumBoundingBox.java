package com.ezzenix.engine.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FrustumBoundingBox {
    private Vector3f min;
    private Vector3f max;

    public boolean isShown = false;

    public FrustumBoundingBox(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    public boolean isInsideFrustum(Matrix4f viewProjectionMatrix) {
        Vector4f[] corners = {
                new Vector4f(min.x, min.y, min.z, 1.0f),
                new Vector4f(min.x, min.y, max.z, 1.0f),
                new Vector4f(min.x, max.y, min.z, 1.0f),
                new Vector4f(min.x, max.y, max.z, 1.0f),
                new Vector4f(max.x, min.y, min.z, 1.0f),
                new Vector4f(max.x, min.y, max.z, 1.0f),
                new Vector4f(max.x, max.y, min.z, 1.0f),
                new Vector4f(max.x, max.y, max.z, 1.0f)
        };

        for (Vector4f corner : corners) {
            viewProjectionMatrix.transform(corner);

            if (isOutsideFrustum(corner, viewProjectionMatrix)) {
                return false;
            }
        }

        return true;
    }

    private boolean isOutsideFrustum(Vector4f point, Matrix4f viewProjectionMatrix) {
        Vector4f[] frustumPlanes = getFrustumPlanes(viewProjectionMatrix);

        for (Vector4f plane : frustumPlanes) {
            if (plane.dot(point) < 0) {
                return true;
            }
        }

        return false;
    }

    private Vector4f[] getFrustumPlanes(Matrix4f viewProjectionMatrix) {
        // Extract the frustum planes from the view-projection matrix
        // The order of planes depends on your matrix conventions

        Vector4f[] frustumPlanes = new Vector4f[6];

        // Extract the rows of the matrix
        Vector4f row0 = new Vector4f();
        Vector4f row1 = new Vector4f();
        Vector4f row2 = new Vector4f();
        Vector4f row3 = new Vector4f();

        viewProjectionMatrix.getRow(0, row0);
        viewProjectionMatrix.getRow(1, row1);
        viewProjectionMatrix.getRow(2, row2);
        viewProjectionMatrix.getRow(3, row3);

        // Extract the frustum planes
        frustumPlanes[0] = row3.add(row0); // Left plane
        frustumPlanes[1] = row3.sub(row0); // Right plane
        frustumPlanes[2] = row3.add(row1); // Bottom plane
        frustumPlanes[3] = row3.sub(row1); // Top plane
        frustumPlanes[4] = row3.add(row2); // Near plane
        frustumPlanes[5] = row3.sub(row2); // Far plane

        // Normalize the planes
        for (int i = 0; i < 6; i++) {
            frustumPlanes[i].normalize3();
        }

        return frustumPlanes;
    }
}
