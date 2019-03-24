package com.dezzy.skorp3.game;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Dimension;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import com.dezzy.skorp3.game.graphics.Renderer;
import com.dezzy.skorp3.game.state.GameState;
import com.dezzy.skorp3.game.state.StateUpdateObject;
import com.dezzy.skorp3.logging.Logger;
import com.dezzy.skorp3.messaging.Message;
import com.dezzy.skorp3.messaging.MessageHandler;
import com.dezzy.skorp3.messaging.MessageHandlerRegistry;
import com.dezzy.skorp3.messaging.meta.Sends;
import com.dezzy.skorp3.messaging.meta.SendsTo;

public class Game implements Runnable {
	private volatile boolean isRunning = false;
	private GameState currentState = GameState.INTRO;
	private Renderer renderer;
	private final long windowHandle;
	private List<Consumer<Float>> injectedUpdaters = new ArrayList<Consumer<Float>>();
	
	public Game(final GLFWKeyCallbackI keyCallback, final GLFWCursorPosCallbackI cursorPosCallback, final Dimension initialSize) {
		Logger.log("Starting Skorp 3");
		windowHandle = initializeOpenGL(keyCallback, cursorPosCallback, initialSize);
		initializeState();
	}
	
	public Game setRenderer(final Renderer _renderer) {
		renderer = _renderer;
		return this;
	}
	
	public void injectUpdater(final Consumer<Float> updater) {		
		injectedUpdaters.add(updater);
	}
	
	private void initializeState() {
		postNewState();
	}
	
	private long initializeOpenGL(final GLFWKeyCallbackI keyCallback, final GLFWCursorPosCallbackI cursorPosCallback, final Dimension initialSize) {
		long window;
		
		GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(Logger.getLogger()).set();
		glfwSetErrorCallback(errorCallback);
		
		if (!glfwInit()) {
			Logger.error("Unable to initialize GLFW");
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		glfwDefaultWindowHints();
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		window = glfwCreateWindow(initialSize.width, initialSize.height, "Skorp 3", NULL, NULL);
		if (window == NULL) {
			Logger.error("GLFW window could not be created");
			throw new RuntimeException("GLFW window could not be created");
		}
		
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCursorPosCallback(window, cursorPosCallback);
		
		
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			
			glfwGetWindowSize(window, pWidth, pHeight);
			
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			
			glfwSetWindowPos(window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}
		
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		
		GL.createCapabilities();
		
		GL33.glEnable(GL33.GL_DEPTH_TEST);
		GL33.glDepthFunc(GL33.GL_LESS);
		
		return window;
	}
	
	private void postNewState() {
		StateUpdateObject suo = new StateUpdateObject(currentState, currentState);
		MessageHandlerRegistry.GAME_STATE_HANDLER.dispatch(new Message("STATE_UPDATE", suo));
	}
	
	private void render(float delta) {
		preRender();
		renderer.render();
		postRender();
	}
	
	private void preRender() {
		
	}
	
	private void postRender() {
		GLFW.glfwSwapBuffers(windowHandle);
		GLFW.glfwPollEvents();
	}
	
	private void terminate() {
		isRunning = false;
		terminateGLFW();
	}
	
	private void terminateGLFW() {
		glfwFreeCallbacks(windowHandle);
		glfwDestroyWindow(windowHandle);
		
		glfwTerminate();
	}
	
	private void update(float delta) {
		runInjectedUpdaters(delta);
	}
	
	private void runInjectedUpdaters(float delta) {
		for (int i = injectedUpdaters.size() - 1; i >= 0; i--) {
			injectedUpdaters.get(i).accept(delta);
		}
	}
	
	public void startAndRun() {
		isRunning = true;
		run();
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	private static final float FRAMES = 60.0f;
	private static final float NANOS_PER_FRAME = 1E9f / FRAMES;
	
	@Sends({"QUIT"})
	@SendsTo("Global")
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		float delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / NANOS_PER_FRAME;
			lastTime = now;
			
			if (delta >= 1.0) {
				update(delta);
				updates++;
				delta--;
			}
			
			if (!glfwWindowShouldClose(windowHandle)) {
				render(delta);
			} else {
				MessageHandler.dispatchGlobal(new Message("QUIT", null));
				Logger.log("Quitting GLFW");
				isRunning = false;
				terminate();
			}
			
			frames++;
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				updates = 0;
				frames = 0;
			}
		}
	}
}
