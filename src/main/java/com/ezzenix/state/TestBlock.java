package com.ezzenix.state;

import com.ezzenix.state.property.BooleanProperty;
import com.ezzenix.state.property.IntProperty;
import com.ezzenix.state.property.Properties;
import com.ezzenix.state.property.Property;

public class TestBlock {
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final IntProperty AGE = Properties.AGE;

	public final StateManager stateManager;

	public TestBlock() {
		stateManager = new StateManager(getProperties());

		State state = stateManager.getDefaultState();
		state.dumpProperties();
		state = state.with(OPEN, false);
		state.dumpProperties();
		state = state.with(AGE, 5);
		state.dumpProperties();

		State state1 = stateManager.getDefaultState().with(AGE, 3);
		State state2 = stateManager.getDefaultState();
		System.out.println(state1 == state2);
	}

	public Property<?>[] getProperties() {
		return new Property[]{OPEN, AGE};
	}
}
