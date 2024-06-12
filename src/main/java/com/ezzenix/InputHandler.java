package com.ezzenix;

import com.ezzenix.Game;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.core.Profiler;
import com.ezzenix.engine.physics.Physics;
import com.ezzenix.engine.physics.Raycast;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.entities.Player;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.world.World;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import com.ezzenix.rendering.Camera;
import com.ezzenix.rendering.WorldRenderer;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class InputHandler {
	private int lastMouseX, lastMouseY;
	private boolean isFirstUpdate = true;

	public InputHandler() {
		handleMouse();

		Input.mouseButton2Down(() -> {
			Raycast result = Game.getInstance().getPlayer().raycast();
			if (result != null && result.hitFace != null) {
				Vector3i faceNormal = result.hitFace.getNormal();
				BlockPos blockPos = result.blockPos.add(faceNormal.x, faceNormal.y, faceNormal.z);
				if (blockPos.isValid()) {

					BoundingBox blockBoundingBox = Physics.getBlockBoundingBox(blockPos);
					for (Entity entity : Game.getInstance().getEntities()) {
						if (entity.boundingBox.getIntersection(blockBoundingBox).length() > 0) return;
					}

					Game.getInstance().getWorld().setBlock(blockPos, BlockType.GRASS_BLOCK);
				}
			}
		});

		Input.mouseButton1Down(() -> {
			Raycast result = Game.getInstance().getPlayer().raycast();
			if (result != null) {
				Game.getInstance().getWorld().setBlock(result.blockPos, BlockType.AIR);
			}
		});

		Input.keyUp(GLFW_KEY_F5, () -> {
			Camera camera = Game.getInstance().getCamera();
			camera.thirdPerson = !camera.thirdPerson;
		});

		Input.keyUp(GLFW_KEY_F4, Profiler::dump);

		Input.keyUp(GLFW_KEY_KP_1, () -> {
			BlockPos blockPos = Game.getInstance().getPlayer().getBlockPos();
			if (blockPos.isValid()) {
				Game.getInstance().getWorld().setBlock(blockPos, BlockType.STONE);
			}
		});

		Input.keyUp(GLFW_KEY_ESCAPE, () -> {
			glfwSetWindowShouldClose(Game.getInstance().getWindow().getId(), true);
		});

		Input.keyDown(GLFW_KEY_SPACE, () -> {
			Game.getInstance().getPlayer().applyImpulse(new Vector3f(0, 5, 0));
		});
	}

	public void handleInput(long window) {
		handleKeyboard(window);
	}

	public void handleMouse() {
		long window = Game.getInstance().getWindow().getId();
		Player player = Game.getInstance().getPlayer();

		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		glfwSetCursorPosCallback(window, (windowHandle, xpos, ypos) -> {
			int mouseX = (int) Math.round(xpos);
			int mouseY = (int) Math.round(ypos);

			if (isFirstUpdate) {
				lastMouseX = mouseX;
				lastMouseY = mouseY;
				isFirstUpdate = false;
			}

			int deltaX = mouseX - lastMouseX;
			int deltaY = mouseY - lastMouseY;

			lastMouseX = mouseX;
			lastMouseY = mouseY;

			float sensitivity = 0.35f;
			player.addYaw(deltaX * sensitivity * -1);
			player.addPitch(deltaY * sensitivity * -1);
		});
	}

	public void handleKeyboard(long window) {
		Player player = Game.getInstance().getPlayer();

		float speed = 7f * Scheduler.getDeltaTime();
		if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
			speed *= 20;
		}

		Vector3f lookVector = new Vector3f(0.0f, 0.0f, -1.0f);
		Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
		Quaternionf orientation = new Quaternionf()
			.rotateAxis((float) Math.toRadians(player.getYaw() + 180), upVector)
			.rotateAxis((float) Math.toRadians(0), new Vector3f(1.0f, 0.0f, 0.0f));
		lookVector.set(0.0f, 0.0f, -1.0f).rotate(orientation);
		upVector.set(0.0f, 1.0f, 0.0f);
		Vector3f rightVector = new Vector3f();
		lookVector.cross(upVector, rightVector).normalize();

		//System.out.println(lookVector.toString(new DecimalFormat("#.#")));

		Vector3f movementVector = new Vector3f();

		if (Input.getKey(GLFW_KEY_W)) {
			movementVector.add(new Vector3f(lookVector.x, 0, lookVector.z).mul(speed));
		}
		if (Input.getKey(GLFW_KEY_A)) {
			movementVector.add(new Vector3f(rightVector.x, 0, rightVector.z).mul(-speed));
		}
		if (Input.getKey(GLFW_KEY_S)) {
			movementVector.add(new Vector3f(lookVector.x, 0, lookVector.z).mul(-speed));
		}
		if (Input.getKey(GLFW_KEY_D)) {
			movementVector.add(new Vector3f(rightVector.x, 0, rightVector.z).mul(speed));
		}

		/*
		if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
			player.getPosition().add(new Vector3f(0, speed, 0));
		}
		if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
			player.getPosition().add(new Vector3f(0, -speed, 0));
		}
		 */

		//Vector3f velocity = player.getVelocity();
		//velocity.set(Math.max(movementVector.x, velocity.x), velocity.y, Math.max(movementVector.z, velocity.z));

		player.applyImpulse(movementVector);

		//player.setPosition(player.getPosition().add(movementVector));
	}
}
