package com.ezzenix.rendering;

import com.ezzenix.Block;
import com.ezzenix.Game;
import com.ezzenix.utilities.BlockPos;
import com.ezzenix.utilities.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

public class GameRenderer {
    private final HashMap<BlockPos, Block> blocks = new HashMap<>();
    void addBlock(BlockPos position) {
        blocks.put(position, new Block(position));
    }

    private final Camera camera;

    public GameRenderer() {
        camera = new Camera();
        addBlock(new BlockPos(0, 0, 0));
    }

    public Camera getCamera() { return this.camera; }

    public void updateProjection(int width, int height) {
        float fov = 45.0f;
        float aspectRatio = (float)width / (float)height;
        float near = 0.1f;
        float far = 100.0f;

        glMatrixMode(GL11.GL_PROJECTION);
        glLoadIdentity();

        float yScale = (float) (1.0 / Math.tan(Math.toRadians(fov / 2.0)));
        float xScale = yScale / aspectRatio;

        glFrustum(-near * xScale, near * xScale, -near * yScale, near * yScale, near, far);

        glMatrixMode(GL11.GL_MODELVIEW);

        System.out.println("Projection updated!");
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

        float[] viewMatrix = camera.getViewMatrix();
        GL11.glMultMatrixf(viewMatrix);

        drawCube();

        glPopMatrix();

        glfwSwapBuffers(window); // swap the color buffers

        int error = glGetError();
        if (error != GL_NO_ERROR) {
            System.err.println("OpenGL Error: " + error);
        }
    }

    private void drawCube() {
        // Front face
        glBegin(GL_QUADS);
        glColor3f(1f, 1f, 1f);
        glVertex3f(-0.5f, -0.5f, 0.5f); // Bottom-left
        glVertex3f(0.5f, -0.5f, 0.5f);  // Bottom-right
        glVertex3f(0.5f, 0.5f, 0.5f);   // Top-right
        glVertex3f(-0.5f, 0.5f, 0.5f);  // Top-left
        glEnd();

        // Back face
        glBegin(GL_QUADS);
        glColor3f(0.5f, 0f, 1f);
        glVertex3f(-0.5f, -0.5f, -0.5f); // Bottom-left
        glVertex3f(0.5f, -0.5f, -0.5f);  // Bottom-right
        glVertex3f(0.5f, 0.5f, -0.5f);   // Top-right
        glVertex3f(-0.5f, 0.5f, -0.5f);  // Top-left
        glEnd();

        // Left face
        glBegin(GL_QUADS);
        glColor3f(0.5f, 0.5f, 0f);
        glVertex3f(-0.5f, -0.5f, 0.5f);  // Bottom-front
        glVertex3f(-0.5f, -0.5f, -0.5f); // Bottom-back
        glVertex3f(-0.5f, 0.5f, -0.5f);  // Top-back
        glVertex3f(-0.5f, 0.5f, 0.5f);   // Top-front
        glEnd();

        // Right face
        glBegin(GL_QUADS);
        glColor3f(0f, 0.5f, 0.5f);
        glVertex3f(0.5f, -0.5f, 0.5f);  // Bottom-front
        glVertex3f(0.5f, -0.5f, -0.5f); // Bottom-back
        glVertex3f(0.5f, 0.5f, -0.5f);  // Top-back
        glVertex3f(0.5f, 0.5f, 0.5f);   // Top-front
        glEnd();

        // Top face
        glBegin(GL_QUADS);
        glColor3f(0f, 1f, 0f);
        glVertex3f(-0.5f, 0.5f, 0.5f);  // Front-left
        glVertex3f(0.5f, 0.5f, 0.5f);   // Front-right
        glVertex3f(0.5f, 0.5f, -0.5f);  // Back-right
        glVertex3f(-0.5f, 0.5f, -0.5f); // Back-left
        glEnd();

        // Bottom face
        glBegin(GL_QUADS);
        glColor3f(0.5f, 0f, 0f);
        glVertex3f(-0.5f, -0.5f, 0.5f);  // Front-left
        glVertex3f(0.5f, -0.5f, 0.5f);   // Front-right
        glVertex3f(0.5f, -0.5f, -0.5f);  // Back-right
        glVertex3f(-0.5f, -0.5f, -0.5f); // Back-left
        glEnd();
    }
}
