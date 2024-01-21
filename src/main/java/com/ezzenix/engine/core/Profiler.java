package com.ezzenix.engine.core;

public class Profiler {
	public static class Profile implements AutoCloseable {
		String identifier;
		long startTime;

		public Profile(String identifier) {
			this.identifier = identifier;
			this.startTime = System.nanoTime();
		}

		@Override
		public void close() {
			System.out.println("[Profiler] " + this.identifier + " closed after " + (System.nanoTime() - this.startTime) + " nanoseconds");
		}
	}

	private static Profile currentProfile = null;

	public static Profile begin(String identifier) {
		if (currentProfile != null) {
			System.err.println("[Profiler] Can not begin profile " + identifier + " because " + currentProfile.identifier + " is still active");
			return null;
		}
		currentProfile = new Profile(identifier);
		return currentProfile;
	}

	public static void pop() {
		if (currentProfile == null) {
			System.err.println("[Profiler] Can not pop because there is no active profile");
			return;
		}
		currentProfile.close();
		currentProfile = null;
	}
}
