package com.ezzenix.engine.gui;

import com.ezzenix.engine.gui.components.GuiComponent;

import java.util.ArrayList;
import java.util.List;

public class Gui {
	private static final List<GuiComponent> components = new ArrayList<>();
	public static void registerComponent(GuiComponent component) {
		components.add(component);
	}
	public static void unregisterComponent(GuiComponent component) {
		components.remove(component);
	}

	public static void render() {
		for (GuiComponent component : components) {
			if (component.shouldRebuild) {
				component.rebuild();
				component.shouldRebuild = false;
			}
			component.render();
		}
	}

	public static void rebuildAll() {
		for (GuiComponent component : components) {
			component.rebuild();
		}
	}

	public enum SizeConstraint {
		XY, XX, YY
	}
}
