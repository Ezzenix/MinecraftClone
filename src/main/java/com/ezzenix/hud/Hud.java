package com.ezzenix.hud;

import com.ezzenix.Game;
import com.ezzenix.opengl.Shader;
import org.joml.Matrix4f;

import java.awt.*;

import static org.lwjgl.opengl.GL20.*;

public class Hud {
    FontRenderer fontRenderer;
    TextComponent fpsText;
    TextComponent positionText;

    Shader textShader;

    public Matrix4f hudProjectionMatrix = new Matrix4f().setOrtho2D(0, 500, 0, 500);

    public Hud() {
        this.textShader = new Shader("text.vert", "text.frag");


        this.fontRenderer = new FontRenderer(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        fpsText = new TextComponent(fontRenderer, "FPS: 0", 10, 10, 1f);
        //positionText = new TextComponent(fontRenderer, "Hello", 10, 10 + 22, 1f);
    }

    public void render(long window) {
        fpsText.text = "FPS: " + Game.getInstance().fps;
        /*
        Vector3f position = Game.getInstance().getRenderer().getCamera().getPosition();
        positionText.text = "X: " + (Math.round(position.x * 10) / 10) + " Y: " + (Math.round(position.y * 10) / 10) + " Z: " + (Math.round(position.z * 10) / 10);
        */

        this.textShader.use();

        fpsText.render();
        //positionText.render();

        glUseProgram(0);
    }
}
