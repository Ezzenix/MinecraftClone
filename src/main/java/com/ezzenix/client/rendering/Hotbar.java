package com.ezzenix.client.rendering;

import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.client.gui.library.components.GuiBlockIcon;
import com.ezzenix.client.gui.library.components.GuiFrame;
import com.ezzenix.client.gui.library.components.GuiText;
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
			item.position = new UDim2(0.5f, i * 55f, 0.5f, 0);
			item.size = UDim2.fromScale(1f, 1f);
			item.color = new Vector3f(0.05f, 0.05f, 0.05f);
			item.transparency = 0.3f;
			item.sizeConstraint = Gui.SizeConstraint.YY;
			item.adornee = holder;

			GuiBlockIcon image = new GuiBlockIcon();
			image.anchorPoint = new Vector2f(0.5f, 0.5f);
			image.position = UDim2.fromScale(0.5f, 0.5f);
			image.size = UDim2.fromScale(0.6f, 0.6f);
			image.adornee = item;

			GuiText text = new GuiText();
			text.anchorPoint = new Vector2f(0.5f, 0.5f);
			text.position = UDim2.fromScale(0.09f, 0.18f);
			text.size = UDim2.fromScale(0.5f, 0.5f);
			text.text = Integer.toString(i + 5);
			text.textAlign = Gui.TextAlign.Center;
			text.fontSize = 11;
			text.adornee = item;
		}
	}
}
