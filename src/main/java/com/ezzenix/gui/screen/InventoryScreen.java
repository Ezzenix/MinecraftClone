package com.ezzenix.gui.screen;

import com.ezzenix.Client;
import com.ezzenix.gui.screen.handledscreen.HandledScreen;

public class InventoryScreen extends HandledScreen {
	public InventoryScreen() {
		super(Client.getPlayer().inventory);
	}
}
