package com.ezzenix.entities;

import com.ezzenix.math.BoundingBox;
import org.joml.Vector3f;

public class EntityDimensions {
	private float width;
	private float height;
	private float eyeHeight;

	public EntityDimensions(float width, float height, float eyeHeight) {
		this.width = width;
		this.height = height;
		this.eyeHeight = eyeHeight;
	}

	public BoundingBox getBoxAt(float x, float y, float z) {
		float w = this.width / 2f;
		return new BoundingBox(x - w, y, z - w, x + w, y + height, z + w);
	}

	public BoundingBox getBoxAt(Vector3f pos) {
		return getBoxAt(pos.x, pos.y, pos.z);
	}

	public float width() {
		return this.width;
	}

	public float height() {
		return this.height;
	}

	public float eyeHeight() {
		return this.eyeHeight;
	}
}
