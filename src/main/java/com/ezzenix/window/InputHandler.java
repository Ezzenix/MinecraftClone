package com.ezzenix.window;

import com.ezzenix.Game;
import com.ezzenix.rendering.Camera;
import com.ezzenix.rendering.GameRenderer;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import javax.swing.text.NumberFormatter;
import java.nio.*;
import java.text.DecimalFormat;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    private int lastMouseX, lastMouseY;
    private boolean isFirstUpdate = true;

    public InputHandler() {

    }

    public void handleInput(long window) {
        handleMouse(window);
        handleKeyboard(window);
    }

    public void handleMouse(long window) {
        GameRenderer renderer = Game.getInstance().getRenderer();
        Camera camera = renderer.getCamera();

        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, x, y);

        int mouseX = (int) Math.round(x.get());
        int mouseY = (int) Math.round(y.get());

        if (isFirstUpdate) {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            isFirstUpdate = false;
        }

        int deltaX = mouseX - lastMouseX;
        int deltaY = mouseY - lastMouseY;

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        float sensitivity = 0.5f;
        camera.addYaw(deltaX * sensitivity);
        camera.addPitch(deltaY * sensitivity * -1);
    }

    private long lastPrint = 0;
    public void printWithCooldown(String str) {
        if (System.currentTimeMillis() > lastPrint + 1000) {
            lastPrint = System.currentTimeMillis();

            System.out.println(str);
        }
    }

    public void handleKeyboard(long window) {
        GameRenderer renderer = Game.getInstance().getRenderer();
        Camera camera = renderer.getCamera();

        float speed = 0.1f;

        //Vector3f lookVector = camera.getLookVector();
        //System.out.println(lookVector.toString(new DecimalFormat("#.##")));

        //System.out.println(camera.getPitch());

        Vector3f lookVector = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Quaternionf orientation = new Quaternionf()
                .rotateAxis((float) Math.toRadians(camera.getYaw()), upVector)
                .rotateAxis((float) Math.toRadians(camera.getPitch()), new Vector3f(1.0f, 0.0f, 0.0f));
        lookVector.set(0.0f, 0.0f, -1.0f).rotate(orientation);
        upVector.set(0.0f, 1.0f, 0.0f).rotate(orientation);
        Vector3f rightVector = new Vector3f();
        lookVector.cross(upVector, rightVector).normalize();

        //printWithCooldown("LookVector: " + lookVector.toString(new DecimalFormat("#.##")) + "\nPosition: " + camera.getPosition().toString(new DecimalFormat("#.##")) + "\nDirection: " + (camera.getYaw()+180f));

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

        //System.out.println(movementVector.mul(lookVector).toString(new DecimalFormat("#.##")));

        //System.out.println(movementVector.mul(lookVector.mul(speed)));
        //camera.setPosition(camera.getPosition().add(movementVector.mul(lookVector.mul(speed))));
    }
}
