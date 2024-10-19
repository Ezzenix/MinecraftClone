package com.ezzenix.state;

import com.ezzenix.state.property.Property;
import com.google.common.collect.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateManager<S extends State<S>> {
	private final ImmutableSortedMap<String, Property<?>> properties;
	private final ImmutableList<S> states;

	public interface Factory<S> {
		S create(Map<Property<?>, Comparable<?>> properties);
	}

	public StateManager(Property<?>[] properties, Factory<S> factory) {
		Map<String, Property<?>> namedProperties = Maps.newHashMap();
		for (Property<?> property : properties) {
			namedProperties.put(property.getName(), property);
		}

		this.properties = ImmutableSortedMap.copyOf(namedProperties);

		Map<Map<Property<?>, Comparable<?>>, S> statesMap = new HashMap<>();
		List<S> statesList = new ArrayList<>();

		// Generate all possible combinations of properties
		List<Map<Property<?>, Comparable<?>>> allCombinations = generatePropertyCombinations();
		for (Map<Property<?>, Comparable<?>> combination : allCombinations) {
			S state = factory.create(combination);
			statesMap.put(combination, state);
			statesList.add(state);
		}

		// Precompute state transitions
		for (S state : statesList) {
			Table<Property<?>, Comparable<?>, S> transitionTable = HashBasedTable.create();
			for (Property<?> property : this.properties.values()) {
				for (Comparable<?> value : property.getValues()) {
					if (!value.equals(state.getProperties().get(property))) {
						S nextState = statesMap.get(state.toMapWith(property, value));
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

	public S getDefaultState() {
		return states.getFirst();
	}
}
