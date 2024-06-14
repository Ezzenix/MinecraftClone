package com.ezzenix.engine;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Scheduler {
	private static final ConcurrentLinkedQueue<SchedulerRunnable> runnables = new ConcurrentLinkedQueue<>();
	private static float deltaTime = (float) 1 / 60;
	private static long lastUpdate = System.nanoTime();

	private static double lastDrawTime = Double.MIN_VALUE;

	private static final int FPS_BUFFER_SIZE = 60;
	private static final List<Float> fpsBuffer = new ArrayList<>();

	private static final long START_TIME = System.currentTimeMillis();

	public static void update() {
		deltaTime = (System.nanoTime() - lastUpdate) / 1_000_000_000f;
		lastUpdate = System.nanoTime();

		fpsBuffer.add((float) Math.round(1f / deltaTime));
		if (fpsBuffer.size() > FPS_BUFFER_SIZE) fpsBuffer.remove(0);

		for (SchedulerRunnable schedulerRunnable : runnables) {
			if (schedulerRunnable.canRun()) {
				schedulerRunnable.run();
			}
		}
	}

	public static void limitFps(int fps) {
		double d = lastDrawTime + 1.0 / (double) fps;
		double e = GLFW.glfwGetTime();
		while (e < d) {
			GLFW.glfwWaitEventsTimeout(d - e);
			e = GLFW.glfwGetTime();
		}
		lastDrawTime = e;
	}

	public static float getDeltaTime() {
		return deltaTime;
	}

	/**
	 * Returns average frames per second
	 */
	public static float getFps() {
		return (float) fpsBuffer.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
	}

	/**
	 * Returns amount of seconds the game has been running
	 */
	public static float getClock() {
		long t = System.currentTimeMillis() - START_TIME;
		return t / 1000f;
	}

	public static SchedulerRunnable bindToUpdate(Runnable runnable) {
		SchedulerRunnable schedulerRunnable = new SchedulerRunnable(runnable);
		runnables.add(schedulerRunnable);
		return schedulerRunnable;
	}

	public static SchedulerRunnable setInterval(Runnable runnable, long interval) {
		SchedulerRunnable schedulerRunnable = new SchedulerRunnable(runnable, interval);
		runnables.add(schedulerRunnable);
		return schedulerRunnable;
	}

	public static class SchedulerRunnable {
		private final Runnable runnable;
		private final long interval;
		private long lastRun;

		public SchedulerRunnable(Runnable runnable, long interval) {
			this.lastRun = 0;
			this.interval = interval * 1000000;
			this.runnable = runnable;
		}

		public SchedulerRunnable(Runnable runnable) {
			this(runnable, 0L);
		}

		public void run() {
			this.lastRun = System.nanoTime();
			this.runnable.run();
		}

		public boolean canRun() {
			return System.nanoTime() > (this.lastRun + this.interval);
		}

		public void dispose() {
			runnables.remove(this);
		}
	}
}
