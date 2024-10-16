package com.ezzenix.state;

import com.ezzenix.state.property.Property;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;

import java.util.Map;
import java.util.Objects;

public class State {
	private final Map<Property<?>, Comparable<?>> properties;
	private Table<Property<?>, Comparable<?>, State> transitionTable;

	public State(Map<Property<?>, Comparable<?>> properties) {
		this.properties = properties;
	}

	public Map<Property<?>, Comparable<?>> getProperties() {
		return properties;
	}

	public <T extends Comparable<T>> Comparable<?> get(Property<T> property) {
		return this.properties.get(property);
	}

	public <T extends Comparable<T>, V extends T> State with(Property<T> property, V value) {
		Comparable<?> comparable = this.properties.get(property);

		if (comparable == null) {
			throw new IllegalStateException("Cannot set property " + property.getClass().getSimpleName() + " as it does not exist");
		}

		if (comparable.equals(value)) {
			return this;
		}

		State nextState = this.transitionTable.get(property, value);
		if (nextState == null) {
			throw new IllegalStateException("Cannot set property " + property.getClass().getSimpleName() + " to " + value + " as it is not an allowed value");
		}

		return nextState;
	}

	public void dumpProperties() {
		System.out.println("----------------------------------");
		for (Property<?> property : properties.keySet()) {
			System.out.println(property.getName() + ": " + get(property));
		}
		System.out.println("----------------------------------");
	}

	public Map<Property<?>, Comparable<?>> toMapWith(Property<?> property, Comparable<?> value) {
		Map<Property<?>, Comparable<?>> map = new Reference2ObjectArrayMap<>(this.properties);
		map.put(property, value);
		return map;
	}

	public void setTransitionTable(Table<Property<?>, Comparable<?>, State> transitionTable) {
		this.transitionTable = transitionTable;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		State state = (State) o;
		return Objects.equals(properties, state.properties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(properties);
	}
}
