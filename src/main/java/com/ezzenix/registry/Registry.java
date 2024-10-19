package com.ezzenix.registry;

import java.util.ArrayList;
import java.util.List;

public class Registry<T> {
	List<T> list = new ArrayList<>();

	public void register(T value) {
		list.add(value);
	}

	public List<T> getAll() {
		return list;
	}
}
