package com.ezzenix.client.options;

import com.ezzenix.client.Client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GameOptions {
	private final List<Option<?>> options = new ArrayList<>();
	private final File optionsFile;


	public BoolOption IS_COOL = new BoolOption("is_cool", true);
	public BoolOption SMOOTH_LIGHTING = new BoolOption("smooth_lighting", true);
	public BoolOption IS_NOOB = new BoolOption("is_noob", false);
	public BoolOption IS_PRO = new BoolOption("is_pro", true);


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

				System.out.println("Loaded option " + name + " with value " + option.value);
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public abstract class Option<T> {
		String name;
		T defaultValue;
		T value;

		public Option(String name, T defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
			this.value = defaultValue;

			options.add(this);
		}

		public abstract void read(String string);

		public abstract String stringify();
	}

	public class BoolOption extends Option<Boolean> {
		public BoolOption(String name, boolean defaultValue) {
			super(name, defaultValue);
		}

		public void read(String string) {
			this.value = Boolean.parseBoolean(string);
		}

		public String stringify() {
			return Boolean.toString(this.value);
		}
	}
}
