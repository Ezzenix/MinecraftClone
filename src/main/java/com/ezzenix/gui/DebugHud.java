package com.ezzenix.gui;

import com.ezzenix.Client;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.entities.player.Player;
import com.ezzenix.enums.Direction;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.physics.Raycast;
import com.ezzenix.rendering.Renderer;
import org.joml.Vector3f;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.opengl.GL30.*;

public class DebugHud {
	private static boolean isEnabled = false;

	private static final AllocationRateCalculator allocationRateCalculator = new AllocationRateCalculator();

	public static boolean isEnabled() {
		return isEnabled;
	}

	public static void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}

	private static void renderLines(List<String> lines, boolean rightSide) {
		int i = 0;
		for (String line : lines) {
			int x = 6;
			if (rightSide) {
				int textWidth = Gui.FONT_RENDERER.getWidth(line);
				x = Client.getWindow().getWidth() - textWidth - 6;
			}

			Gui.drawText(line, x, 6 + i * 18, Color.WHITE);

			i++;
		}
	}

	public static void render() {
		if (!isEnabled()) return;

		//long startTime = System.nanoTime();

		renderLines(getLeftText(), false);
		renderLines(getRightText(), true);

		//System.out.println("Rendered DebugHud in " + (System.nanoTime() - startTime));
	}

	private static List<String> getLeftText() {
		List<String> lines = new ArrayList<>();

		Player player = Client.getPlayer();
		Vector3f position = player.getPosition();
		Vector3f velocity = player.getVelocity();
		BlockPos blockPos = new BlockPos(position);
		ChunkPos chunkPos = new ChunkPos(blockPos);

		lines.add(String.format("FPS: %.0f (min: %.0f, max: %.0f)", Scheduler.getAverageFps(), Scheduler.getMinFps(), Scheduler.getMaxFps()));

		lines.add("");

		lines.add(String.format("XYZ: %.3f / %.3f / %.3f", position.x, position.y, position.z));
		lines.add(String.format("Block: %d / %d / %d", blockPos.x, blockPos.y, blockPos.z));
		lines.add(String.format("Chunk: %d / %d", chunkPos.x, chunkPos.z));
		lines.add(String.format("Facing: %s (%.1f / %.1f)", Direction.fromYaw(player.getYaw()).getName(), player.getYaw(), player.getPitch()));

		lines.add("");

		lines.add(String.format("Grounded: %s", player.isGrounded ? "Yes" : "No"));
		lines.add(String.format("Velocity: %.3f / %.3f / %.3f", velocity.x, velocity.y, velocity.z));
		lines.add(String.format("Chunks rendered: %d", Renderer.getWorldRenderer().chunksRenderedCount));

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
		lines.add(String.format("Mem: % 2d%% %01d/%01dMB", usedMemory * 100L / maxMemory, toMB(usedMemory), toMB(maxMemory)));
		lines.add(String.format(Locale.ROOT, "Allocation: %dMB/s", DebugHud.toMB(allocationRateCalculator.get(usedMemory))));
		lines.add(String.format("Off-Heap: %dMB", toMB(offHeapMemory)));

		lines.add("");

		lines.add(String.format("Display: %dx%d (%s)", Client.getWindow().getWidth(), Client.getWindow().getHeight(), glGetString(GL_VENDOR)));
		lines.add(String.format(Objects.requireNonNullElse(glGetString(GL_RENDERER), "")));
		lines.add(String.format(Objects.requireNonNullElse(glGetString(GL_VERSION), "")));

		Raycast result = Client.getPlayer().raycast();
		if (result != null) {
			lines.add("");
			lines.add("Target Position: " + result.blockPos.x + " " + result.blockPos.y + " " + result.blockPos.z);
			lines.add("Target Block: " + result.blockType.getName());
		}

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
