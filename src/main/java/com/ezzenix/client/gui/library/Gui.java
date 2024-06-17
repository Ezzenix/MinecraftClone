package com.ezzenix.client.gui.library;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.library.components.GuiComponent;
import com.ezzenix.client.gui.screen.Screen;
import com.ezzenix.engine.Input;

import java.util.ArrayList;
import java.util.List;

public class Gui {
	private static final List<GuiComponent> components = new ArrayList<>();

	static {
		// Rebuild all components when window size changes
		//Client.getWindow().sizeChanged.connect(() -> {
		//	for (GuiComponent component : components) {
		//		component.rebuild();
		//	}
		//});

		Input.mouseButton1Up(() -> {
			int x = Client.getMouse().getX();
			int y = Client.getMouse().getY();

			for (GuiComponent component : components) {
				component.mouseButton1Activated(x, y);
			}
		});
	}

	public static void registerComponent(GuiComponent component) {
		components.add(component);
	}

	public static void unregisterComponent(GuiComponent component) {
		components.remove(component);
	}

	public static void disposeScreenComponents(Screen screen) {
		List<GuiComponent> toDispose = new ArrayList<>();
		for (GuiComponent component : components) {
			if (component.screen == screen) {
				toDispose.add(component);
			}
		}
		for (GuiComponent component : toDispose) {
			component.dispose();
		}
	}

	public static void render() {
		for (GuiComponent component : components) {
			if (component.shouldRebuild) {
				component.rebuild();
				component.shouldRebuild = false;
			}
			if (!component.isStandalone) {
				component.render();
			}
		}
	}

	public enum SizeConstraint {
		XY, XX, YY
	}

	public enum TextAlign {
		Left, Center, Right
	}
}
