package com.ezzenix.window;

import com.ezzenix.Game;
import com.ezzenix.rendering.Camera;
import com.ezzenix.rendering.GameRenderer;
import com.ezzenix.utilities.Vector3;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

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
        camera.addPitch(deltaY * sensitivity);
    }

    public void handleKeyboard(long window) {
        GameRenderer renderer = Game.getInstance().getRenderer();
        Camera camera = renderer.getCamera();

        float speed = 0.02f;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.addPosition(new Vector3(0, 0, -speed));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.addPosition(new Vector3(speed, 0f, 0f));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.addPosition(new Vector3(0f, 0f, speed));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.addPosition(new Vector3(-speed, 0f, 0f));
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            camera.addPosition(new Vector3(0, speed, 0));
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            camera.addPosition(new Vector3(0, -speed, 0));
        }
    }
}
