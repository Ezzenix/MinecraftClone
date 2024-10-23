package com.ezzenix.entities.player;

import com.ezzenix.Client;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.entities.Entity;
import com.ezzenix.inventory.Inventory;
import com.ezzenix.inventory.ItemStack;
import com.ezzenix.item.Item;
import com.ezzenix.item.Items;
import com.ezzenix.world.World;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
	public boolean isMoving = false;

	private MovementInput movementInput;

	public Inventory inventory;
	private int handSlot;

	public Player(World world, Vector3f position) {
		super(world, position);

		this.movementInput = new MovementInput();

		this.handSlot = 0;
		this.inventory = new Inventory(9 * 4);
		this.inventory.setSlot(0, new ItemStack(Items.GRASS_BLOCK, 64));
		this.inventory.setSlot(1, new ItemStack(Items.STONE, 32));
		this.inventory.setSlot(2, new ItemStack(Items.DIRT, 16));
		this.inventory.setSlot(3, new ItemStack(Items.SAND, 4));
		this.inventory.setSlot(4, new ItemStack(Items.GLASS, 2));
		this.inventory.setSlot(5, new ItemStack(Items.OAK_PLANKS, 16));
		this.inventory.setSlot(6, new ItemStack(Items.OAK_LEAVES, 16));
		this.inventory.setSlot(7, new ItemStack(Items.POPPY, 16));
		this.inventory.setSlot(8, new ItemStack(Items.OAK_LOG, 16));

		for (int i = 0; i < 9; i++) {
			int slot = i;
			Input.keyDown(49 + i, () -> {
				this.setHandSlot(slot);
			});
		}
	}

	public void setHandSlot(int slot) {
		this.handSlot = slot;

		ItemStack stack = this.inventory.getSlot(slot);
		if (stack != null) {
			Client.getHud().sendActionbar(stack.item.name);
		}
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

	@Override
	public float getGravity() {
		return 0.98f;
	}

	public void updateMovement() {
		this.movementInput.update();

		this.isMoving = movementInput.pressingForward || movementInput.pressingBack || movementInput.pressingLeft || movementInput.pressingRight;
		this.setSneaking(movementInput.sneaking);


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
		if (movementInput.sprinting) speed *= 1.8f;
		if (movementInput.sneaking) speed *= 0.5f;
		if (this.isFlying) speed *= 4;

		Vector3f movementVector = new Vector3f();
		movementVector.add(new Vector3f(lookVector.x, 0, lookVector.z).mul(speed * movementInput.movementForward));
		movementVector.add(new Vector3f(rightVector.x, 0, rightVector.z).mul(speed * movementInput.movementSideways));

		if (this.isFlying) {
			int i = movementInput.jumping == movementInput.sneaking ? 0 : (movementInput.jumping ? 1 : -1);
			this.applyImpulse(0, i * 40 * Scheduler.getDeltaTime(), 0);
		} else {
			if (movementInput.jumping) {
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
