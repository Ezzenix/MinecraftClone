package com.ezzenix.state;

import com.ezzenix.state.property.Property;
import com.google.common.collect.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateManager {
	private final ImmutableSortedMap<String, Property<?>> properties;
	private final ImmutableList<State> states;

	public StateManager(Property<?>[] properties) {
		Map<String, Property<?>> namedProperties = Maps.newHashMap();
		for (Property<?> property : properties) {
			namedProperties.put(property.getName(), property);
		}

		this.properties = ImmutableSortedMap.copyOf(namedProperties);

		Map<Map<Property<?>, Comparable<?>>, State> statesMap = new HashMap<>();
		List<State> statesList = new ArrayList<>();

		// Generate all possible combinations of properties
		List<Map<Property<?>, Comparable<?>>> allCombinations = generatePropertyCombinations();
		for (Map<Property<?>, Comparable<?>> combination : allCombinations) {
			State state = new State(combination);
			statesMap.put(combination, state);
			statesList.add(state);
		}

		// Precompute state transitions
		for (State state : statesList) {
			Table<Property<?>, Comparable<?>, State> transitionTable = HashBasedTable.create();
			for (Property<?> property : this.properties.values()) {
				for (Comparable<?> value : property.getValues()) {
					if (!value.equals(state.getProperties().get(property))) {
						State nextState = statesMap.get(state.toMapWith(property, value));
						transitionTable.put(property, value, nextState);
					}
				}
			}
			state.setTransitionTable(transitionTable);
		}

		this.states = ImmutableList.copyOf(statesList);
	}

	private List<Map<Property<?>, Comparable<?>>> generatePropertyCombinations() {
		List<Map<Property<?>, Comparable<?>>> combinations = new ArrayList<>();
		List<Property<?>> propertyList = new ArrayList<>(properties.values());

		// Recursively generate all possible combinations of property values
		generateCombinations(propertyList, 0, new HashMap<>(), combinations);
		return combinations;
	}

	private void generateCombinations(List<Property<?>> properties, int index, Map<Property<?>, Comparable<?>> currentCombination, List<Map<Property<?>, Comparable<?>>> combinations) {
		if (index >= properties.size()) {
			combinations.add(new HashMap<>(currentCombination));
			return;
		}

		Property<?> property = properties.get(index);
		for (Comparable<?> value : property.getValues()) {
			currentCombination.put(property, value);
			generateCombinations(properties, index + 1, currentCombination, combinations);
		}
	}

	public State getDefaultState() {
		return states.getFirst();
	}
}
