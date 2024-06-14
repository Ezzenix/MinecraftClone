package com.ezzenix.engine;

import java.util.ArrayList;
import java.util.List;

public class Signal {
	List<Runnable> connections = new ArrayList<>();

	public Signal() {
	}

	public void connect(Runnable runnable) {
		connections.add(runnable);
	}

	public void fire() {
		for (Runnable runnable : connections) {
			runnable.run();
		}
	}
}
