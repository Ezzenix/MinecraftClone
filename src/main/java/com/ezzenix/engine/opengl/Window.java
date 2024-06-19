package com.ezzenix.engine.opengl;

import com.ezzenix.client.Client;
import com.ezzenix.client.resource.ResourceManager;
import com.ezzenix.engine.Signal;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class Window {
	private String title = "Window";
	private long handle;

	private int windowedX;
	private int windowedY;

	private int width = 1280;
	private int height = 720;
	private int windowedWidth = width;
	private int windowedHeight = height;

	private boolean vsync = false;
	private boolean isFullscreen = false;
	private boolean isFocused = true;
	private boolean isMinimized = true;

	private final boolean useDebugContext;

	public final Signal sizeChanged = new Signal();

	public Window(boolean useDebugContext) {
		System.out.println(useDebugContext ? "Creating window with debug context!" : "Creating window!");
		this.useDebugContext = useDebugContext;
		this.initWindow();
	}

	public Window() {
		this(false);
	}

	private void initWindow() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (useDebugContext) {
			System.setProperty("org.lwjgl.util.Debug", "true");

			Configuration.DEBUG.set(true);
			Configuration.DEBUG_FUNCTIONS.set(true);
			Configuration.DEBUG_LOADER.set(true);
			Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
			Configuration.DEBUG_MEMORY_ALLOCATOR_FAST.set(true);
			Configuration.DEBUG_STACK.set(true);
		}

		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_FALSE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		// Create the window
		handle = glfwCreateWindow(width, height, title, isFullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);
		if (handle == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwMakeContextCurrent(handle);
		createCapabilities();

		updateWindow();

		System.out.println("LWJGL " + Version.getVersion());
		System.out.println("OpenGl " + glGetString(GL_VERSION));

		// Listen for changes
		glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChanged);
		glfwSetWindowPosCallback(this.handle, this::onWindowPosChanged);
		glfwSetWindowFocusCallback(this.handle, this::onWindowFocusChanged);
		glfwSetWindowIconifyCallback(this.handle, this::onWindowMinimizedChanged);

		if (useDebugContext) {
			// Enable OpenGL debug output
			if (getCapabilities().OpenGL43) {
				glEnable(GL_DEBUG_OUTPUT);
				glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
				glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, (int[]) null, true);
				glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
					if (severity != 33387) {
						System.err.println("\nOpenGL debug message:");
						System.err.println("\tType: " + type);
						System.err.println("\tSeverity: " + severity);
						System.err.println("\tMessage: " + memUTF8(message));
					}
				}, 0);
				System.out.println("OpenGL debug was set up successfully");
			} else {
				System.err.println("OpenGL 4.3 or higher is required for debug output.");
			}
		}
	}

	private void onWindowSizeChanged(long window, int width, int height) {
		if (width == 0 || height == 0) return; // minimized

		this.width = width;
		this.height = height;
		glViewport(0, 0, width, height);

		if (!this.isFullscreen) {
			this.windowedWidth = width;
			this.windowedHeight = height;
		}

		Client.getMouse().resetDelta();

		if (Client.getScreen() != null) {
			Client.getScreen().dispose();
			Client.getScreen().init(width, height);
		}

		sizeChanged.fire();
		updateWindow();
	}

	private void onWindowPosChanged(long window, int x, int y) {
		if (!this.isFullscreen()) {
			this.windowedX = x;
			this.windowedY = y;
		}
	}

	private void onWindowFocusChanged(long window, boolean focused) {
		this.isFocused = focused;
	}

	private void onWindowMinimizedChanged(long window, boolean minimized) {
		this.isMinimized = minimized;
	}

	public void setTitle(String title) {
		glfwSetWindowTitle(handle, title);
		this.title = title;
	}

	public void centerWindow() {
		GLFWVidMode monitorResolution = glfwGetVideoMode(glfwGetPrimaryMonitor());
		if (monitorResolution == null) return;
		glfwSetWindowPos(handle, (monitorResolution.width() - width) / 2, (monitorResolution.height() - height) / 2);
	}

	public void setVSync(boolean enabled) {
		this.vsync = enabled;
		glfwSwapInterval(enabled ? 1 : 0);
	}

	public void setFullscreen(boolean fullscreen) {
		this.isFullscreen = fullscreen;
		updateWindow();
	}

	public void showWindow() {
		glfwShowWindow(handle);
	}

	public void updateWindow() {
		if (this.isFullscreen) {
			long monitor = glfwGetPrimaryMonitor();
			GLFWVidMode vidMode = glfwGetVideoMode(monitor);

			if (vidMode == null)
				throw new RuntimeException("vidMode is NULL");

			glfwSetWindowMonitor(handle, monitor, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
		} else {
			glfwSetWindowMonitor(handle, NULL, windowedX, windowedY, this.windowedWidth, this.windowedHeight, -1);
		}
	}

	public void setIcon(String path) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			GLFWImage image = GLFWImage.malloc(stack);
			GLFWImage.Buffer buffer = GLFWImage.malloc(1);

			BufferedImage loadedImage = ResourceManager.loadImage(path);
			ByteBuffer imageBuffer = ResourceManager.parseBufferedImage(loadedImage);

			image.set(loadedImage.getWidth(), loadedImage.getHeight(), imageBuffer);
			buffer.put(0, image);
			glfwSetWindowIcon(handle, buffer);
		}
	}

	public void cleanup() {
		glfwFreeCallbacks(handle);
		glfwDestroyWindow(handle);
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}

	/*
		Getters
	*/

	public long getHandle() {
		return handle;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(handle);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public boolean isFullscreen() {
		return this.isFullscreen;
	}

	public boolean isFocused() {
		return this.isFocused;
	}

	public boolean isMinimized() {
		return this.isMinimized;
	}

	public boolean isVsyncEnabled() {
		return this.vsync;
	}
}
