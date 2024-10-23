package com.ezzenix.world;

import com.ezzenix.Client;
import com.ezzenix.engine.Scheduler;

public class TickManager {

	private float lastTick = Scheduler.getClock();

	private final float TICKS_PER_SECOND = 20;
	private final float TIME_PER_TICK = 1 / TICKS_PER_SECOND;

	public void update() {
		float now = Scheduler.getClock();
		if (now > lastTick + TIME_PER_TICK) {
			tick();
			while (lastTick < now) {
				lastTick += TIME_PER_TICK;
			}
		}
	}

	private void tick() {
		World world = Client.getWorld();
		world.tick();
	}
}
