package com.ezzenix.client.util;

import com.ezzenix.client.Client;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.lwjgl.opengl.GL11.*;

public class Screenshot {
	static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);

	private static BufferedImage capture() {
		int WIDTH = Client.getWindow().getWidth();
		int HEIGHT = Client.getWindow().getHeight();

		int[] pixels = new int[WIDTH * HEIGHT];
		int bindex;
		ByteBuffer fb = ByteBuffer.allocateDirect(WIDTH * HEIGHT * 3);

		glReadPixels(0, 0, WIDTH, HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, fb);

		BufferedImage imageIn = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < pixels.length; i++) {
			bindex = i * 3;
			pixels[i] =
				((fb.get(bindex) << 16)) +
					((fb.get(bindex + 1) << 8)) +
					((fb.get(bindex + 2)));
		}
		imageIn.setRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);

		AffineTransform at = AffineTransform.getScaleInstance(1, -1);
		at.translate(0, -imageIn.getHeight(null));

		return new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR).filter(imageIn, null);
	}

	private static void saveScreenshot(BufferedImage image, File file) {
		try {
			ImageIO.write(image, "PNG", file);
		} catch (Exception e) {
			System.out.println("Error when capturing screenshot: " + e);
		}
	}

	private static File getScreenshotFile(File directory) {
		String string = dateTimeFormatter.format(ZonedDateTime.now());
		int i = 1;
		File file;
		while ((file = new File(directory, string + (String) (i == 1 ? "" : "_" + i) + ".png")).exists()) {
			++i;
		}
		return file;
	}

	public static void takeScreenshot(File directory, String fileName) {
		File file = fileName != null ? new File(directory, fileName) : getScreenshotFile(directory);
		saveScreenshot(capture(), file);
		Client.getHud().chatHud.addMessage("Saved screenshot " + file.getName());
	}
}
