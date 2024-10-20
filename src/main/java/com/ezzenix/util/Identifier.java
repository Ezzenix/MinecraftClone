package com.ezzenix.util;

public class Identifier {
	private final String path;

	private Identifier(String path) {
		this.path = path;
	}

	public static Identifier of(String path) {
		return new Identifier(path);
	}

	public String getPath() {
		return this.path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Identifier)) {
			return false;
		} else {
			Identifier identifier = (Identifier) o;
			return this.path.equals(identifier.path);
		}
	}

	@Override
	public int hashCode() {
		return this.path.hashCode();
	}
}
