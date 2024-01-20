package com.ezzenix.input;

import com.ezzenix.Game;
import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.game.entities.Player;
import com.ezzenix.rendering.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;

public class InputHandler {
    private int lastMouseX, lastMouseY;
    private boolean isFirstUpdate = true;

    public InputHandler() {
        handleMouse();

        AtomicBoolean wireframeMode = new AtomicBoolean(false);
        glfwSetKeyCallback(Game.getInstance().getWindow().getId(), (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_F5 && action == GLFW_RELEASE) {
                Camera camera = Game.getInstance().getCamera();
                camera.thirdPerson = !camera.thirdPerson;
            }
            if (key == GLFW_KEY_Z && action == GLFW_RELEASE) {
                wireframeMode.set(!wireframeMode.get());
                if (wireframeMode.get()) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    glDisable(GL_DEPTH_TEST);
                    glDisable(GL_CULL_FACE);
                } else {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    glEnable(GL_DEPTH_TEST);
                    glEnable(GL_CULL_FACE);
                }
            }
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
            speed *= 7;
        }

        Vector3f lookVector = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Quaternionf orientation = new Quaternionf()
                .rotateAxis((float) Math.toRadians(player.getYaw()+180), upVector)
                .rotateAxis((float) Math.toRadians(0), new Vector3f(1.0f, 0.0f, 0.0f));
        lookVector.set(0.0f, 0.0f, -1.0f).rotate(orientation);
        upVector.set(0.0f, 1.0f, 0.0f);
        Vector3f rightVector = new Vector3f();
        lookVector.cross(upVector, rightVector).normalize();

        //System.out.println(lookVector.toString(new DecimalFormat("#.#")));

        Vector3f movementVector = new Vector3f();

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            movementVector.add(new Vector3f(lookVector.x, 0, lookVector.z).mul(speed));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            movementVector.add(new Vector3f(rightVector.x, 0, rightVector.z).mul(-speed));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            movementVector.add(new Vector3f(lookVector.x, 0, lookVector.z).mul(-speed));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            movementVector.add(new Vector3f(rightVector.x, 0, rightVector.z).mul(speed));
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            player.getPosition().add(new Vector3f(0, speed, 0));
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
            player.getPosition().add(new Vector3f(0, -speed, 0));
        }

        player.getPosition().add(movementVector);
    }
}
