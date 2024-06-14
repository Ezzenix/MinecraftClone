package com.ezzenix.client.gui;

import com.ezzenix.Game;
import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.client.gui.library.components.GuiText;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.game.entities.Player;
import com.ezzenix.game.enums.Direction;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.opengl.GL11.*;

public class DebugHud {
	private static final List<GuiText> textComponents = new ArrayList<>();
	private static boolean isEnabled = false;

	private static final AllocationRateCalculator allocationRateCalculator = new AllocationRateCalculator();

	static {
		Scheduler.setInterval(DebugHud::update, 100);
	}

	public static boolean isEnabled() {
		return isEnabled;
	}

	public static void setEnabled(boolean enabled) {
		isEnabled = enabled;
		if (!enabled) cleanup();
	}

	private static void renderLines(List<String> lines, boolean rightSide) {
		int i = 0;
		for (String line : lines) {
			GuiText text = new GuiText();
			text.text = line;
			text.position = UDim2.fromOffset(6, i * 18);

			if (rightSide) {
				text.textAlign = Gui.TextAlign.Right;
				text.anchorPoint = new Vector2f(1, 0);
				text.position.scaleX = 1;
				text.position.offsetX = -6;
			}

			textComponents.add(text);
			i++;
		}
	}

	private static void update() {
		if (!isEnabled()) return;

		//long startTime = System.currentTimeMillis();

		cleanup();
		renderLines(getLeftText(), false);
		renderLines(getRightText(), true);

		//System.out.println("Updated DebugHud in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	private static void cleanup() {
		for (GuiText component : textComponents) {
			component.dispose();
		}
	}

	private static List<String> getLeftText() {
		List<String> lines = new ArrayList<>();

		Player player = Game.getInstance().getPlayer();
		Vector3f position = player.getPosition();
		Vector3f velocity = player.getVelocity();
		BlockPos blockPos = BlockPos.from(position);
		ChunkPos chunkPos = ChunkPos.from(blockPos);

		lines.add("FPS: " + (int) Scheduler.getFps());

		lines.add("");

		lines.add(String.format("XYZ: %.3f / %.3f / %.3f", position.x, position.y, position.z));
		lines.add(String.format("Block: %d / %d / %d", blockPos.x, blockPos.y, blockPos.z));
		lines.add(String.format("Chunk: %d / %d", chunkPos.x, chunkPos.z));
		lines.add(String.format("Facing: %s (%.1f / %.1f)", Direction.fromYaw(player.getYaw()).getName(), player.getYaw(), player.getPitch()));

		lines.add("");

		lines.add(String.format("Grounded: %s", player.isGrounded ? "Yes" : "No"));
		lines.add(String.format("Velocity: %.3f / %.3f / %.3f", velocity.x, velocity.y, velocity.z));

		return lines;
	}

	private static List<String> getRightText() {
		List<String> lines = new ArrayList<>();

		Runtime runtime = Runtime.getRuntime();

		long maxMemory = runtime.maxMemory();
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long usedMemory = totalMemory - freeMemory;
		long offHeapMemory = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();

		lines.add(String.format("Java: %s", System.getProperty("java.version")));
		lines.add(String.format("Mem: % 2d%% %03d/%03dMB", usedMemory * 100L / maxMemory, toMB(usedMemory), toMB(maxMemory)));
		lines.add(String.format(Locale.ROOT, "Allocation: %dMB/s", DebugHud.toMB(allocationRateCalculator.get(usedMemory))));
		lines.add(String.format("Off-Heap: %dMB", toMB(offHeapMemory)));

		lines.add("");

		lines.add(String.format("Display: %dx%d (%s)", Game.getInstance().getWindow().getWidth(), Game.getInstance().getWindow().getHeight(), glGetString(GL_VENDOR)));
		lines.add(String.format(Objects.requireNonNullElse(glGetString(GL_RENDERER), "")));
		lines.add(String.format(Objects.requireNonNullElse(glGetString(GL_VERSION), "")));

		return lines;
	}

	private static long toMB(long bytes) {
		return bytes / 1024L / 1024L;
	}

	static class AllocationRateCalculator {
		private static final List<GarbageCollectorMXBean> GARBAGE_COLLECTORS = ManagementFactory.getGarbageCollectorMXBeans();
		private long lastCalculated = 0L;
		private long allocatedBytes = -1L;
		private long collectionCount = -1L;
		private long allocationRate = 0L;

		AllocationRateCalculator() {
		}

		long get(long allocatedBytes) {
			long l = System.currentTimeMillis();
			if (l - this.lastCalculated < 500L) {
				return this.allocationRate;
			}
			long m = AllocationRateCalculator.getCollectionCount();
			if (this.lastCalculated != 0L && m == this.collectionCount) {
				double d = (double) TimeUnit.SECONDS.toMillis(1L) / (double) (l - this.lastCalculated);
				long n = allocatedBytes - this.allocatedBytes;
				this.allocationRate = Math.round((double) n * d);
			}
			this.lastCalculated = l;
			this.allocatedBytes = allocatedBytes;
			this.collectionCount = m;
			return this.allocationRate;
		}

		private static long getCollectionCount() {
			long l = 0L;
			for (GarbageCollectorMXBean garbageCollectorMXBean : GARBAGE_COLLECTORS) {
				l += garbageCollectorMXBean.getCollectionCount();
			}
			return l;
		}
	}
}
