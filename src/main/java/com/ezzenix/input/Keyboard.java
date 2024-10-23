package com.ezzenix.input;

import com.ezzenix.Client;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.core.Profiler;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.gui.chat.ChatScreen;
import com.ezzenix.gui.screen.InventoryScreen;
import com.ezzenix.gui.screen.PauseScreen;
import com.ezzenix.math.BlockPos;
import com.ezzenix.util.Screenshot;
import org.joml.Vector3f;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard {
	long lastSpaceClicked = 0;

	public Keyboard() {
		Input.keyDown(GLFW_KEY_F5, () -> {
			if (Client.isPaused()) return;

			Client.getOptions().thirdPerson = !Client.getOptions().thirdPerson;
		});

		Input.keyDown(GLFW_KEY_F1, () -> {
			if (Client.isPaused()) return;

			Client.getOptions().hudHidden = !Client.getOptions().hudHidden;
		});

		Input.keyDown(GLFW_KEY_F4, Profiler::dump);

		Input.keyDown(GLFW_KEY_KP_1, () -> {
			BlockPos blockPos = Client.getPlayer().getBlockPos();
			if (blockPos.isValid()) {
				Client.getWorld().setBlockState(blockPos, Blocks.STONE.getDefaultState());
			}
		});

		Input.keyDown(GLFW_KEY_KP_2, () -> {
			Client.getPlayer().teleport(new Vector3f(0, 100, 0));
		});

		Input.keyDown(GLFW_KEY_F6, () -> {
			Client.getPlayer().noClip = !Client.getPlayer().noClip;
		});

		Input.keyDown(GLFW_KEY_ESCAPE, () -> {
			if (Client.getScreen() == null) {
				Client.setScreen(new PauseScreen());
			} else {
				Client.setScreen(null);
			}
		});

		Input.keyDown(GLFW_KEY_T, () -> {
			if (Client.getScreen() != null) return;
			Client.setScreen(new ChatScreen());
		});

		Input.keyDown(GLFW_KEY_F11, () -> {
			Window window = Client.getWindow();
			window.setFullscreen(!window.isFullscreen());
		});

		Input.keyDown(GLFW_KEY_F2, () -> {
			File screenshotDirectory = new File(Client.getDirectory(), "screenshots");
			screenshotDirectory.mkdir();
			Screenshot.takeScreenshot(screenshotDirectory, null);
		});

		Input.keyDown(GLFW_KEY_E, () -> {
			if (Client.getScreen() instanceof InventoryScreen) {
				Client.setScreen(null);
			} else {
				Client.setScreen(new InventoryScreen());
			}
		});

		glfwSetCharCallback(Client.getWindow().getHandle(), (long window, int codePoint) -> {
			if (Client.getScreen() != null) {
				Client.getScreen().charTyped(codePoint);
			}
		});

		Input.keyDown(GLFW_KEY_SPACE, () -> {
			if (Client.isPaused()) return;

			long now = System.currentTimeMillis();
			if (now - lastSpaceClicked < 300) {
				Client.getPlayer().isFlying = !Client.getPlayer().isFlying;
			}
			lastSpaceClicked = now;
		});
	}
}
