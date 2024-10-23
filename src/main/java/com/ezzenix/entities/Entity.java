package com.ezzenix.entities;

import com.ezzenix.engine.Scheduler;
import com.ezzenix.entities.player.Player;
import com.ezzenix.math.BlockPos;
import com.ezzenix.physics.Raycast;
import com.ezzenix.util.Util;
import com.ezzenix.world.World;
import org.joml.Math;
import org.joml.Vector3f;

public class Entity {
	private final Vector3f pos;
	private final Vector3f velocity = new Vector3f();
	private float yaw = 0f;
	private float pitch = 0f;

	private World world;

	private final EntityDimensions dimensions;

	public boolean noClip = false;

	public boolean isGrounded = false;
	public boolean isInFluid = false;
	public boolean isFlying = false;
	private boolean isSneaking = false;

	public Entity(World world, Vector3f pos) {
		this.pos = pos;

		// sneaking: 0.6, 1.5, 1.27
		this.dimensions = new EntityDimensions(0.6f, 1.8f, 1.62f);

		this.world = world;
		world.getEntities().add(this);
	}

	public void tick() {

	}

	public Vector3f getPos() {
		return this.pos;
	}
	public Vector3f getEyePosition() {
		return new Vector3f(this.pos.x, this.pos.y + getDimensions().eyeHeight(), this.pos.z);
	}

	public EntityDimensions getDimensions() {
		return this.dimensions;
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

	public void setPos(Vector3f pos) {
		this.pos.set(pos);
	}
	public void setPos(float x, float y, float z) {
		this.pos.set(x, y, z);
	}

	public void setYaw(float yaw) {
		while (yaw > 180) yaw -= 360;
		while (yaw < 180) yaw += 360;
		this.yaw = (yaw + 180.0f) % 360.0f - 180.0f;
	}
	public void setPitch(float pitch) {
		this.pitch = Math.max(-90f, Math.min(90f, pitch));
	}

	public void setSneaking(boolean sneaking) {
		this.isSneaking = sneaking;
	}

	public boolean isSneaking() {
		return this.isSneaking;
	}

	public void addYaw(float offset) {
		this.setYaw(this.yaw + offset);
	}
	public void addPitch(float offset) {
		this.setPitch(this.pitch + offset);
	}

	public BlockPos getBlockPos() {
		return new BlockPos(pos);
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
		this.setPos(position);
		this.getVelocity().set(0);
	}

	public Raycast raycast() {
		float interactionRange = 6;
		return Raycast.create(getWorld(), getEyePosition(), getLookVector().mul(interactionRange));
	}

	public float getGravity() {
		return 0f;
	}
}
