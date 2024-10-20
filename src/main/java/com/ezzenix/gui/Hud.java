package com.ezzenix.gui;

import com.ezzenix.Client;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.entities.player.Player;
import com.ezzenix.gui.chat.ChatHud;
import com.ezzenix.inventory.ItemStack;

public class Hud {

	public ChatHud chatHud = new ChatHud();

	private String actionbarText = "";
	private float actionbarTime = 0;

	//private int i = 0;
	public Hud() {
		//Scheduler.setInterval(() -> {
		//	chatHud.addMessage("Line " + i);
		//	i++;
		//}, 1000);
	}

	private void renderCrosshair() {
		int width = Client.getWindow().getWidth();
		int height = Client.getWindow().getHeight();

		int length = 6;
		int size = 2;

		Gui.drawRect(width / 2 - size / 2, height / 2 - length, size, length * 2, 1, 1, 1, 0.5f);
		Gui.drawRect(width / 2 - length, height / 2 - size / 2, length * 2, size, 1, 1, 1, 0.5f);
	}

	private void renderHotbar() {
		int width = Client.getWindow().getWidth();
		int height = Client.getWindow().getHeight();

		Player player = Client.getPlayer();

		int size = 50;
		int spacing = 5;

		for (int i = -4; i <= 4; i++) {
			int slot = i + 4;
			boolean isHandSlot = player.getHandSlot() == slot;
			ItemStack itemStack = player.inventory.getSlot(slot);

			int bgColor = isHandSlot ? Color.pack(85, 85, 85, 0.5f) : Color.pack(13, 13, 13, 0.5f);

			int x = width / 2 + i * (size + spacing) - size / 2;
			int y = height - size - 15;
			Gui.drawRect(x, y, size, size, bgColor);

			if (itemStack == null) continue;

			//if (isHandSlot) {
			//	Gui.drawCenteredText(itemStack.item.name, width / 2, y - 22, Color.WHITE);
			//}

			Gui.drawStack(itemStack, x + 10, y + 10, size - 20);
		}
	}

	private float getAlpha(float a, float b, float n) {
		return Math.clamp(((n - a) / (b - a)), 0f, 1f);
	}

	private void renderActionbar() {
		float alpha = getAlpha(1.2f, 1.8f, Scheduler.getClock() - this.actionbarTime);
		if (alpha == 1) return;
		int color = Color.pack(1f, 1f, 1f, 1f - alpha);
		Gui.drawCenteredTextWithShadow(this.actionbarText, Client.getWindow().getWidth() / 2, Client.getWindow().getHeight() - 100, color);
	}

	public void sendActionbar(String text) {
		this.actionbarText = text;
		this.actionbarTime = Scheduler.getClock();
	}

	public void render() {
		if (Client.getOptions().hudHidden) return;

		this.renderCrosshair();
		this.renderHotbar();
		this.renderActionbar();
		this.chatHud.render();

		//GuiContext.drawTexture(Renderer.getWorldRenderer().blockTexture, 0, 0, Client.getWindow().getWidth(), Client.getWindow().getHeight());
		//GuiContext.drawBlockIcon(Blocks.GRASS_BLOCK, 0, 0, Math.max(Client.getWindow().getWidth(), Client.getWindow().getHeight()));
	}
}
