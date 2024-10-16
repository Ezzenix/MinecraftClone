package com.ezzenix.state.property;

import java.util.ArrayList;
import java.util.List;

public class IntProperty extends Property<Integer> {
	public IntProperty(String name, int min, int max) {
		super(name);

		List<Integer> values = new ArrayList<>();
		for (int i = min; i <= max; i++) {
			values.add(i);
		}
		this.setValues(values);
	}
}
