package com.ezzenix.game.entities;

import com.ezzenix.Game;
import com.ezzenix.engine.core.Util;
import com.ezzenix.engine.physics.AABB;
import com.ezzenix.engine.physics.Raycast;
import com.ezzenix.game.world.World;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Entity {
	private Vector3f position;
	private Vector3f velocity;
	private float yaw;
	private float pitch;
	private World world;

	public float eyeHeight = 1.5f;

	public AABB aabb;
	public BoundingBox boundingBox;
	public boolean isGrounded;

	public Entity(World world, Vector3f position) {
		this.yaw = 0;
		this.pitch = 0;
		this.position = position;
		this.velocity = new Vector3f();

		this.isGrounded = false;
		this.aabb = new AABB(0.8f, 1.8f);

		this.boundingBox = new BoundingBox();
		this.updateBoundingBox();

		Game.getInstance().getEntities().add(this);

		this.world = world;
	}

	private void updateBoundingBox() {
		float WIDTH = 0.8f;
		float HEIGHT = 1.8f;

		boundingBox.minX = position.x - WIDTH / 2;
		boundingBox.minY = position.y;
		boundingBox.minZ = position.z - WIDTH / 2;
		boundingBox.maxX = position.x + WIDTH / 2;
		boundingBox.maxY = position.y + HEIGHT;
		boundingBox.maxZ = position.z + WIDTH / 2;
	}

	public Vector3f getPosition() {
		return this.position;
	}
	public Vector3f getEyePosition() {
		return new Vector3f(this.position).add(0, this.eyeHeight, 0);
	}

	public Vector3f getVelocity() {
		return this.velocity;
	}
	public void applyImpulse(Vector3f impulse) {
		this.getVelocity().add(impulse);
	}
	public void applyImpulse(float x, float y, float z) {
		this.getVelocity().add(x, y, z);
	}

	public float getYaw() {
		return this.yaw;
	}
	public float getPitch() {
		return this.pitch;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		updateBoundingBox();
	}
	public void setYaw(float yaw) {
		while (yaw > 180) yaw -= 360;
		while (yaw < 180) yaw += 360;
		this.yaw = (yaw + 180.0f) % 360.0f - 180.0f;
	}
	public void setPitch(float pitch) {
		float min = -90f;
		float max = 90f;
		this.pitch = Math.max(min, Math.min(max, pitch));
	}

	public void addYaw(float offset) {
		this.setYaw(this.yaw + offset);
	}
	public void addPitch(float offset) {
		this.setPitch(this.pitch + offset);
	}

	public BlockPos getBlockPos() {
		return BlockPos.from(position);
	}

	public World getWorld() {
		return this.world;
	}

	public Vector3f getLookVector() {
		return Util.getLookVector(getYaw(), getPitch());
	}

	public Raycast raycast() {
		float interactionRange = 5;
		return Raycast.create(getWorld(), getEyePosition(), getLookVector().mul(interactionRange));
	}
}
