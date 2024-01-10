package com.ezzenix.rendering;

import com.ezzenix.Game;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

public class GameRenderer {

    private final Camera camera;

    public GameRenderer() {
        camera = new Camera();
    }

    public Camera getCamera() {
        return this.camera;
    }

    public void render() {
        long window = Game.getInstance().getWindow().getHandle();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glPushMatrix();

        //glTranslatef((float)camera.getPosition().getX(), (float)-camera.getPosition().getY(), (float)camera.getPosition().getZ());
        //glRotatef(camera.getYaw(), 0, 1, 0);
        //glRotatef(camera.getPitch(), 1, 0, 0);
        //glTranslatef((float)-camera.getPosition().getX(), (float)-camera.getPosition().getY(), (float)-camera.getPosition().getZ());
        //glRotatef(-camera.getPitch(), 1, 0, 0);
        //glRotatef(-camera.getYaw(), 0, 1, 0);

        //float[] viewMatrix = camera.getViewMatrix();
        //GL11.glMultMatrixf(viewMatrix);

        glPopMatrix();

        glfwSwapBuffers(window); // swap the color buffers

        int error = glGetError();
        if (error != GL_NO_ERROR) {
            System.err.println("OpenGL Error: " + error);
        }
    }
}
