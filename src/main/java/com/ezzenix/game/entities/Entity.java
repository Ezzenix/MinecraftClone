package com.ezzenix.game.entities;

import com.ezzenix.Game;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.engine.core.Util;
import com.ezzenix.engine.physics.Raycast;
import com.ezzenix.game.world.World;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import org.joml.Math;
import org.joml.Vector3f;

public class Entity {
	private final Vector3f position;
	private final Vector3f velocity = new Vector3f();
	private float yaw = 0f;
	private float pitch = 0f;

	private World world;

	private float eyeHeight = 1.5f;
	public float width = 0.6f;
	public float height = 1.8f;

	public BoundingBox boundingBox;
	public boolean isGrounded = false;
	public boolean isInFluid = false;

	public boolean isFlying = false;
	private boolean isSneaking = false;

	public Entity(World world, Vector3f position) {
		this.position = position;

		this.boundingBox = new BoundingBox();
		this.updateBoundingBox();

		Game.getInstance().getEntities().add(this);

		this.world = world;
	}

	private void updateBoundingBox() {
		boundingBox.minX = position.x - width / 2;
		boundingBox.minY = position.y;
		boundingBox.minZ = position.z - width / 2;
		boundingBox.maxX = position.x + width / 2;
		boundingBox.maxY = position.y + height;
		boundingBox.maxZ = position.z + width / 2;

		if (this.isSneaking) {
			boundingBox.maxY -= 0.2f;
		}
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
	public Vector3f getHorizontalVelocity() {
		return new Vector3f(this.velocity.x, 0, this.velocity.z);
	}

	public float getYaw() {
		return this.yaw;
	}
	public float getPitch() {
		return this.pitch;
	}

	public void setPosition(Vector3f position) {
		this.position.set(position);
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

	public void setSneaking(boolean sneaking) {
		if (this.isSneaking == sneaking) return;
		this.isSneaking = sneaking;
		updateBoundingBox();
	}

	public float getEyeHeight() {
		float height = this.eyeHeight;
		if (isSneaking) {
			height -= 0.1f;
		}
		return height;
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

	public void jump() {
		this.applyImpulse(new Vector3f(0, 8f, 0));
	}

	public void floatUp() {
		this.applyImpulse(new Vector3f(0, 20 * Scheduler.getDeltaTime(), 0));
	}

	public void teleport(Vector3f position) {
		this.setPosition(position);
		this.getVelocity().set(0);
	}

	public Raycast raycast() {
		float interactionRange = 5;
		return Raycast.create(getWorld(), getEyePosition(), getLookVector().mul(interactionRange));
	}
}
