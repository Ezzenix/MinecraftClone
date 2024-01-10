package com.ezzenix.hud;

import com.ezzenix.Game;
import com.ezzenix.rendering.Camera;
import org.joml.Vector3f;

import java.awt.*;

public class Hud {
    FontRenderer fontRenderer;
    TextComponent fpsText;
    TextComponent positionText;

    public Hud() {
        this.fontRenderer = new FontRenderer(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        fpsText = new TextComponent(fontRenderer, "FPS: 0", 10, 10, 1f);
        positionText = new TextComponent(fontRenderer, "Hello", 10, 10 + 22, 1f);
    }

    public void render(long window) {
        fpsText.text = "FPS: " + Game.getInstance().fps;
        Vector3f position = Game.getInstance().getRenderer().getCamera().getPosition();
        positionText.text = "X: " + (Math.round(position.x * 10) / 10) + " Y: " + (Math.round(position.y * 10) / 10) + " Z: " + (Math.round(position.z * 10) / 10);

        fpsText.render();
        positionText.render();
    }
}
