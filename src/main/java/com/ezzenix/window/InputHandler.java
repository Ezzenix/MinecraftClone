package com.ezzenix.window;

import com.ezzenix.Game;
import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.rendering.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    private int lastMouseX, lastMouseY;
    private boolean isFirstUpdate = true;

    public InputHandler() {
        handleMouse();
    }

    public void handleInput(long window) {
        handleKeyboard(window);
    }

    public void handleMouse() {
        long window = Game.getInstance().getWindow().getId();
        Camera camera = Game.getInstance().getCamera();

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
            camera.addYaw(deltaX * sensitivity);
            camera.addPitch(deltaY * sensitivity * -1);
        });
    }

    public void handleKeyboard(long window) {
        Camera camera = Game.getInstance().getCamera();

        float speed = 0.03f * Scheduler.getDeltaTime();
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            speed *= 3;
        }

        Vector3f lookVector = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Quaternionf orientation = new Quaternionf()
                .rotateAxis((float) Math.toRadians(camera.getYaw()), upVector)
                .rotateAxis((float) Math.toRadians(0), new Vector3f(1.0f, 0.0f, 0.0f));
        lookVector.set(0.0f, 0.0f, -1.0f).rotate(orientation);
        upVector.set(0.0f, 1.0f, 0.0f).rotate(orientation);
        Vector3f rightVector = new Vector3f();
        lookVector.cross(upVector, rightVector).normalize();

        Vector3f movementVector = new Vector3f();

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            movementVector.add(new Vector3f(lookVector.z, 0, lookVector.x).mul(speed));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            movementVector.add(new Vector3f(rightVector.z, 0, rightVector.x).mul(speed));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            movementVector.add(new Vector3f(lookVector.z, 0, lookVector.x).mul(-speed));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            movementVector.add(new Vector3f(rightVector.z, 0, rightVector.x).mul(-speed));
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            camera.addPosition(new Vector3f(0, speed, 0));
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
            camera.addPosition(new Vector3f(0, -speed, 0));
        }

        camera.setPosition(camera.getPosition().add(movementVector));
    }
}
