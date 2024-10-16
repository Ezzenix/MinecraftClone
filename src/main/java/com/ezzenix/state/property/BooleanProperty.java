package com.ezzenix.state.property;

import java.util.Arrays;

public class BooleanProperty extends Property<Boolean> {
	public BooleanProperty(String name) {
		super(name);

		this.setValues(Arrays.asList(true, false));
	}
}
