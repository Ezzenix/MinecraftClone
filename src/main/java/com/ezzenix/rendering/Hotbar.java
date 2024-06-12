package com.ezzenix.rendering;

import com.ezzenix.engine.gui.Gui;
import com.ezzenix.engine.gui.UDim2;
import com.ezzenix.engine.gui.components.GuiFrame;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Hotbar {
	public static void initialize() {

		GuiFrame holder = new GuiFrame();
		holder.anchorPoint = new Vector2f(0.5f, 1f);
		holder.position = UDim2.fromScale(0.5f, 0.98f);
		holder.size = UDim2.fromScale(0.45f, 0.07f);
		holder.transparency = 1f;

		for (int i = -4; i <= 4; i++) {
			GuiFrame item = new GuiFrame();
			item.anchorPoint = new Vector2f(0.5f, 0.5f);
			item.position = UDim2.fromScale(0.5f + i * 0.1f, 0.5f);
			item.size = UDim2.fromScale(1f, 1f);
			item.color = new Vector3f(0.05f, 0.05f, 0.05f);
			item.transparency = 0.3f;
			item.sizeConstraint = Gui.SizeConstraint.YY;
			item.adornee = holder;
		}
	}
}
