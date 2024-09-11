package com.ezzenix.entities.player;

import com.ezzenix.client.Client;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.entities.Entity;
import com.ezzenix.inventory.Inventory;
import com.ezzenix.inventory.ItemStack;
import com.ezzenix.item.Item;
import com.ezzenix.world.World;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
	public boolean isMoving = false;

	public Inventory inventory;
	private int handSlot;

	public Player(World world, Vector3f position) {
		super(world, position);

		this.handSlot = 0;
		this.inventory = new Inventory(9 * 4);

		for (int i = 0; i < 9; i++) {
			int slot = i;
			Input.keyDown(49 + i, () -> {
				this.setHandSlot(slot);
			});
		}
	}

	public void setHandSlot(int slot) {
		this.handSlot = slot;
	}

	public int getHandSlot() {
		return this.handSlot;
	}

	public ItemStack getHeldItemStack() {
		return this.inventory.getSlot(this.handSlot);
	}

	public Item getHeldItem() {
		ItemStack itemStack = getHeldItemStack();
		if (itemStack == null) return null;
		return itemStack.item;
	}

	public void updateMovement() {
		if (Client.isPaused()) return;

		// get input
		boolean pressingForward = Input.getKey(GLFW_KEY_W);
		boolean pressingBack = Input.getKey(GLFW_KEY_S);
		boolean pressingLeft = Input.getKey(GLFW_KEY_A);
		boolean pressingRight = Input.getKey(GLFW_KEY_D);

		boolean jumping = Input.getKey(GLFW_KEY_SPACE);
		boolean sneaking = Input.getKey(GLFW_KEY_LEFT_CONTROL);
		boolean sprinting = Input.getKey(GLFW_KEY_LEFT_SHIFT);

		if (Client.focusedTextField != null) {
			pressingForward = false;
			pressingBack = false;
			pressingLeft = false;
			pressingRight = false;
			jumping = false;
			sneaking = false;
			sprinting = false;
		}

		float movementForward = pressingForward == pressingBack ? 0 : (pressingForward ? 1.0f : -1.0f);
		float movementSideways = pressingRight == pressingLeft ? 0 : (pressingRight ? 1.0f : -1.0f);

		this.isMoving = pressingForward || pressingBack || pressingLeft || pressingRight;
		this.setSneaking(sneaking);


		// get vectors
		Vector3f lookVector = new Vector3f(0.0f, 0.0f, -1.0f);
		Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
		Quaternionf orientation = new Quaternionf()
			.rotateAxis(Math.toRadians(this.getYaw() + 180), upVector)
			.rotateAxis(Math.toRadians(0), new Vector3f(1.0f, 0.0f, 0.0f));
		lookVector.set(0.0f, 0.0f, -1.0f).rotate(orientation);
		upVector.set(0.0f, 1.0f, 0.0f);
		Vector3f rightVector = new Vector3f();
		lookVector.cross(upVector, rightVector).normalize();

		// move
		float speed = 13f * Scheduler.getDeltaTime();
		if (sprinting) speed *= 1.8f;
		if (sneaking) speed *= 0.5f;
		if (this.isFlying) speed *= 4;

		Vector3f movementVector = new Vector3f();
		movementVector.add(new Vector3f(lookVector.x, 0, lookVector.z).mul(speed * movementForward));
		movementVector.add(new Vector3f(rightVector.x, 0, rightVector.z).mul(speed * movementSideways));

		if (this.isFlying) {
			int i = jumping == sneaking ? 0 : (jumping ? 1 : -1);
			this.applyImpulse(0, i * 40 * Scheduler.getDeltaTime(), 0);
		} else {
			if (jumping) {
				if (this.isGrounded && !this.isInFluid) {
					this.jump();
				} else if (this.isInFluid) {
					this.floatUp();
				}
			}
		}

		this.applyImpulse(movementVector);
	}
}
