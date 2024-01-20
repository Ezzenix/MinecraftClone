package com.ezzenix.engine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class FileIO {
	// returns resource input stream
	public static InputStream readResource(String resourcePath) {
		ClassLoader classLoader = FileIO.class.getClassLoader();
		return classLoader.getResourceAsStream(resourcePath);
	}


	// returns a list of input streams in directory
	public static List<String> readResourcesIn(String directoryPath) {
		List<String> filenames = new ArrayList<>();

		try {
			ClassLoader classLoader = FileIO.class.getClassLoader();
			Enumeration<URL> resources = classLoader.getResources(directoryPath);

			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				try (
						InputStream in = resource.openStream();
						BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
					String filename;

					while ((filename = br.readLine()) != null) {
						filenames.add(filename);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return filenames;
	}
}
