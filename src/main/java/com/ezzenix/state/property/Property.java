package com.ezzenix.state.property;

import java.util.Collection;

public abstract class Property<T extends Comparable<T>> {
	private final String name;
	private Collection<T> values;

	public Property(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Collection<T> getValues() {
		return this.values;
	}

	public void setValues(Collection<T> values) {
		this.values = values;
	}
}