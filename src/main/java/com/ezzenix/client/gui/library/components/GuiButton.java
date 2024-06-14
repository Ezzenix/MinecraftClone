package com.ezzenix.client.gui.library.components;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.library.GuiUtil;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import org.joml.Runtime;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GuiButton extends GuiComponent {
	public String text;

	public GuiFrame frameComponent;
	public GuiText textComponent;

	private Runnable runnable;

	public GuiButton(Runnable runable) {
		super();

		this.runnable = runable;

		frameComponent = new GuiFrame();
		frameComponent.adornee = this;
		frameComponent.color = new Vector3f(0.1f, 0.1f, 0.1f);
		frameComponent.transparency = 0.1f;
		frameComponent.anchorPoint = new Vector2f(0.5f, 0.5f);
		frameComponent.position = UDim2.fromScale(0.5f, 0.5f);
		frameComponent.size = UDim2.fromScale(1f, 1f);
		frameComponent.standalone();

		textComponent = new GuiText();
		textComponent.adornee = this;
		textComponent.anchorPoint = new Vector2f(0.5f, 0.5f);
		textComponent.position = UDim2.fromScale(0.5f, 0.5f);
		textComponent.size = UDim2.fromScale(1f, 1f);
		textComponent.standalone();
	}

	@Override
	public void render() {
		frameComponent.render();
		textComponent.render();
	}

	@Override
	public void mouseButton1Activated(int x, int y) {
		if (this.isPointWithin(x, y)) {
			this.runnable.run();
		}
	}

	@Override
	public void rebuild() {
		textComponent.text = text;
		textComponent.textAlign = Gui.TextAlign.Center;
	}
}
