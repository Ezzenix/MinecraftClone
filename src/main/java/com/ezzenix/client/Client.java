package com.ezzenix.client;

import com.ezzenix.client.gui.Hud;
import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.library.Screen;

public class Client {

	private static Screen currentScreen;

	private static Mouse mouse;
	private static Keyboard keyboard;
	private static Hud hud;

	public static void initialize() {
		mouse = new Mouse();
		keyboard = new Keyboard();
		hud = new Hud();
	}

	public static void setScreen(Screen screen) {
		if (currentScreen != null) {
			currentScreen.dispose();
			Gui.disposeScreenComponents(currentScreen);
		}

		currentScreen = screen;

		if (screen != null) {
			screen.init();
			getMouse().unlockCursor();
		} else {
			getMouse().lockCursor();
		}
	}

	public static Screen getScreen() {
		return currentScreen;
	}

	public static Mouse getMouse() {
		return mouse;
	}
	public Keyboard getKeyboard() {
		return keyboard;
	}

	public static Hud getHud() {
		return hud;
	}

}
