package com.ezzenix.client.options;

import com.ezzenix.client.Client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameOptions {
	private final List<Option<?>> options = new ArrayList<>();
	private final File optionsFile;

	public boolean hudHidden = false;
	public boolean thirdPerson = false;

	public BoolOption SMOOTH_LIGHTING = new BoolOption("smooth_lighting", true);
	public IntOption MAX_FRAME_RATE = new IntOption("max_frame_rate", 144, 5, 260, 5);
	public IntOption FOV = new IntOption("fov", 75, 30, 120, 1);


	public GameOptions() {
		optionsFile = new File(Client.getDirectory(), "options.txt");
		this.read();
	}

	public void write() {
		try {
			try (final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter((OutputStream) new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));) {
				for (Option<?> option : options) {
					printWriter.println(option.name + ":" + option.stringify());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Option<?> getOptionByName(String name) {
		for (Option<?> option : options) {
			if (option.name.equals(name)) {
				return option;
			}
		}
		return null;
	}

	public void read() {
		try {
			if (!this.optionsFile.exists()) return;

			BufferedReader reader = new BufferedReader(new FileReader(this.optionsFile));
			for (String line : reader.lines().toList()) {
				String[] split = line.split(":");
				if (split.length < 2) continue;

				String name = split[0];
				String stringifiedValue = split[1];
				Option<?> option = getOptionByName(name);
				if (option == null) {
					System.err.println("Found unknown option " + name);
					continue;
				}
				option.read(stringifiedValue);
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public abstract class Option<T> {
		public final String name;
		public final T defaultValue;
		private T value;
		public Consumer<T> changeListener;

		public Option(String name, T defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
			this.value = defaultValue;

			options.add(this);
		}

		public void setValue(T value) {
			this.value = value;
			if (this.changeListener != null) {
				this.changeListener.accept(value);
			}
		}

		public T getValue() {
			return this.value;
		}

		public void onChange(Consumer<T> callback) {
			this.changeListener = callback;
		}

		public abstract void read(String string);

		public abstract String stringify();
	}

	public class BoolOption extends Option<Boolean> {
		public BoolOption(String name, boolean defaultValue) {
			super(name, defaultValue);
		}

		public void read(String string) {
			this.setValue(Boolean.parseBoolean(string));
		}

		public String stringify() {
			return Boolean.toString(this.getValue());
		}
	}

	public class IntOption extends Option<Integer> {
		public int minValue;
		public int maxValue;
		public int increment;

		public IntOption(String name, int defaultValue, int minValue, int maxValue, int increment) {
			super(name, defaultValue);
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.increment = increment;
		}

		public void read(String string) {
			this.setValue(Integer.parseInt(string));
		}

		public String stringify() {
			return Integer.toString(this.getValue());
		}
	}
}
