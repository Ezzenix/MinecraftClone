package com.ezzenix.client.gui;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.chat.ChatHud;
import com.ezzenix.client.rendering.Renderer;
import com.ezzenix.engine.Input;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.math.BlockPos;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;

public class Hud {

	ChatHud chatHud = new ChatHud();

	public Hud() {
		chatHud.addMessage("hello friend!");
		chatHud.addMessage("goodbye friend!");

		Input.keyDown(GLFW_KEY_H, () -> {
			chatHud.addMessage("hello world!");
		});
	}

	private void renderCrosshair() {
		int width = Client.getWindow().getWidth();
		int height = Client.getWindow().getHeight();

		int length = 6;
		int size = 2;

		GuiContext.drawRect(width / 2 - size / 2, height / 2 - length, size, length * 2, 1, 1, 1, 0.5f);
		GuiContext.drawRect(width / 2 - length, height / 2 - size / 2, length * 2, size, 1, 1, 1, 0.5f);
	}

	private void renderHotbar() {
		int width = Client.getWindow().getWidth();
		int height = Client.getWindow().getHeight();

		int size = 50;
		int spacing = 5;

		for (int i = -4; i <= 4; i++) {
			int x = width / 2 + i * (size + spacing);
			int y = height - size - 15;

			GuiContext.drawRect(x, y, size, size, 0.05f, 0.05f, 0.05f, 0.5f);
			//GuiContext.drawCenteredText("O", x + size / 2, y + size / 2, 18, 1f, 1f, 1f);

			GuiContext.drawBlockIcon(BlockType.GRASS_BLOCK, x + 12, y + 12, size - 24);
		}
	}

	public void render() {
		renderCrosshair();
		renderHotbar();
		this.chatHud.render();

		//GuiContext.drawTexture(Renderer.getWorldRenderer().blockTexture, 0, 0, Client.getWindow().getWidth(), Client.getWindow().getHeight());
		//GuiContext.drawBlockIcon(BlockType.GRASS_BLOCK, 0, 0, Math.max(Client.getWindow().getWidth(), Client.getWindow().getHeight()));
	}
}
