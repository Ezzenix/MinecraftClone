package com.ezzenix.client.options;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.Color;
import com.ezzenix.client.gui.Gui;
import com.ezzenix.client.gui.screen.Screen;
import com.ezzenix.client.gui.widgets.ButtonWidget;
import com.ezzenix.client.gui.widgets.SliderWidget;

import java.util.function.Function;

public class OptionsScreen extends Screen {
	private final int widgetWidth = 300;
	private final int widgetHeight = 40;

	public OptionsScreen(Screen parent) {
		super("Options Menu");
		this.parent = parent;
	}

	@Override
	public void init(int width, int height) {
		int buttonWidth = 400;


		this.addWidget(createIntOptionWidget(Client.getOptions().MAX_FRAME_RATE, "FPS", width / 2 - widgetWidth - 20, height / 2 - widgetHeight / 2, (v) -> {
			if (v == 260) {
				return "Unlimited";
			} else {
				return Integer.toString(v);
			}
		}));

		this.addWidget(createIntOptionWidget(Client.getOptions().FOV, "FOV", width / 2 + 20, height / 2 - widgetHeight / 2, (v) -> Integer.toString(v)));


		this.addWidget(new ButtonWidget("Done", width / 2 - buttonWidth / 2, height / 2 + 150, buttonWidth, 40, () -> {
			Client.setScreen(null);
		}));
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		Gui.drawCenteredText("Options", Client.getWindow().getWidth() / 2, Client.getWindow().getHeight() / 2 - 200, Color.WHITE);
	}

	@Override
	public boolean shouldPauseGame() {
		return true;
	}

	@Override
	public void onRemoved() {
		Client.getOptions().write();
	}

	private SliderWidget createIntOptionWidget(GameOptions.IntOption option, String text, int x, int y, Function<Integer, String> stringFormatter) {
		SliderWidget widget = new SliderWidget(text, x, y, widgetWidth, widgetHeight, option.getValue(), option.minValue, option.maxValue, option.increment);
		widget.setCallback(option::setValue);
		widget.setStringFormatter(stringFormatter);
		return widget;
	}
}