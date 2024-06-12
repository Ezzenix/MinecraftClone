package com.ezzenix.math;

import com.ezzenix.hud.LineRenderer;
import org.joml.*;
import org.joml.Math;

public class BoundingBox {
	public float minX;
	public float minY;
	public float minZ;

	public float maxX;
	public float maxY;
	public float maxZ;

	public BoundingBox(float x1, float y1, float z1, float x2, float y2, float z2) {
		minX = Math.min(x1, x2);
		minY = Math.min(y1, y2);
		minZ = Math.min(z1, z2);

		maxX = Math.max(x1, x2);
		maxY = Math.max(y1, y2);
		maxZ = Math.max(z1, z2);
	}

	public BoundingBox(Vector3f p1, Vector3f p2) {
		this(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}

	public BoundingBox() {
		this(0, 0, 0, 0, 0, 0);
	}

	public void render(Vector3f color) {
		LineRenderer.drawLine(new Vector3f(minX, minY, minZ), new Vector3f(maxX, minY, minZ), color);
		LineRenderer.drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, minY, maxZ), color);
		LineRenderer.drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, minY, maxZ), color);
		LineRenderer.drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(maxX, minY, maxZ), color);

		LineRenderer.drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, maxY, minZ), color);
		LineRenderer.drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(minX, maxY, maxZ), color);
		LineRenderer.drawLine(new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, maxY, maxZ), color);
		LineRenderer.drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, maxY, minZ), color);

		LineRenderer.drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(maxX, maxY, minZ), color);
		LineRenderer.drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(minX, maxY, maxZ), color);
		LineRenderer.drawLine(new Vector3f(maxX, maxY, minZ), new Vector3f(maxX, maxY, maxZ), color);
		LineRenderer.drawLine(new Vector3f(minX, maxY, maxZ), new Vector3f(maxX, maxY, maxZ), color);
	}

	public void render() {
		this.render(new Vector3f(0, 1, 0));
	}

	public boolean checkFrustum(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
		Matrix4f combinedMatrix = new Matrix4f();
		combinedMatrix.set(projectionMatrix).mul(viewMatrix);
		return checkFrustum(new FrustumIntersection().set(combinedMatrix));
	}

	public boolean checkFrustum(FrustumIntersection frustumIntersection) {
		return frustumIntersection.testAab(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
	}

	public Vector3f getIntersection(BoundingBox other) {
		// Calculate the intersection
		float xIntersection = Math.min(maxX, other.maxX) - Math.max(minX, other.minX);
		float yIntersection = Math.min(maxY, other.maxY) - Math.max(minY, other.minY);
		float zIntersection = Math.min(maxZ, other.maxZ) - Math.max(minZ, other.minZ);

		if (xIntersection > 0 && yIntersection > 0 && zIntersection > 0) {
			return new Vector3f(xIntersection, yIntersection, zIntersection);
		}

		return new Vector3f(0, 0, 0);
	}
}
