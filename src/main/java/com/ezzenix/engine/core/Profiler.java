package com.ezzenix.engine.core;

import java.util.HashMap;

public class Profiler {
	private static final HashMap<String, Profile> profiles = new HashMap<>();
	private static Profile activeProfile = null;

	private static class Profile {
		String name;
		long startTime = -1;
		long totalTime = 0;

		public Profile(String name) {
			this.name = name;
		}

		public void start() {
			this.startTime = System.nanoTime();
		}

		public void end() {
			long time = System.nanoTime() - this.startTime;
			this.startTime = -1;
			//System.out.println("[Profiler] " + this.name + " closed after " + (time) + " nanoseconds");
			this.totalTime += time;
		}
	}

	private static String formatNanoseconds(long nanoseconds) {
		if (nanoseconds < 1_000) {
			return nanoseconds + " ns";
		} else if (nanoseconds < 1_000_000) {
			return nanoseconds / 1_000.0 + " µs";
		} else if (nanoseconds < 1_000_000_000) {
			return nanoseconds / 1_000_000.0 + " ms";
		} else {
			return nanoseconds / 1_000_000_000.0 + " s";
		}
	}

	/**
	 * Start profile with name
	 */
	public static void begin(String name) {
		if (activeProfile != null) {
			System.err.println("[Profiler] Can not begin profile " + name + " because " + activeProfile.name + " is still active");
			return;
		}
		activeProfile = profiles.computeIfAbsent(name, Profile::new);
		activeProfile.start();
	}

	/**
	 * Stop the active profile
	 */
	public static void end() {
		if (activeProfile == null) {
			System.err.println("[Profiler] Can not end because there is no active profile");
			return;
		}
		activeProfile.end();
		activeProfile = null;
	}

	/**
	 * Print out information about all profilers
	 */
	public static void dump() {
		System.out.println("--- Profiler dump ---");
		for (Profile profile : profiles.values()) {
			System.out.println(profile.name + ": " + formatNanoseconds(profile.totalTime));
		}
		System.out.println("---------------------");
	}
}
