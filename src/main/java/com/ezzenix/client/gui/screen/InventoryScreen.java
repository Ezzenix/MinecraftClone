package com.ezzenix.client.gui.screen;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.screen.handledscreen.HandledScreen;

public class InventoryScreen extends HandledScreen {
	public InventoryScreen() {
		super(Client.getPlayer().inventory);
	}
}
