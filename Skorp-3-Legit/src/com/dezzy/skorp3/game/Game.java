package com.dezzy.skorp3.game;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
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
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Dimension;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

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
	final long windowHandle;
	
	public Game(final GLFWKeyCallbackI keyCallback, final GLFWCursorPosCallbackI cursorPosCallback, final Dimension initialSize) {
		windowHandle = initializeOpenGL(keyCallback, cursorPosCallback, initialSize);
		initializeState();
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
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		window = glfwCreateWindow(initialSize.width, initialSize.height, "Window Test", NULL, NULL);
		if (windowHandle == NULL) {
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
		
		return window;
	}
	
	private void postNewState() {
		StateUpdateObject suo = new StateUpdateObject(currentState, currentState);
		MessageHandlerRegistry.GAME_STATE_HANDLER.dispatch(new Message("STATE_UPDATE", suo));
	}
	
	private void render(float delta) {
		preRender();
		
		postRender();
	}
	
	private void preRender() {
		GL.createCapabilities();
	}
	
	@Sends({"QUIT"})
	@SendsTo("Global")
	private void postRender() {
		if (!glfwWindowShouldClose(windowHandle)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glfwSwapBuffers(windowHandle);
			glfwPollEvents();
		} else {
			MessageHandler.dispatchGlobal(new Message("QUIT", null));
			Logger.log("Quitting GLFW");
			isRunning = false;
			terminateGLFW();
		}
	}
	
	private void terminateGLFW() {
		glfwFreeCallbacks(windowHandle);
		glfwDestroyWindow(windowHandle);
		
		glfwTerminate();
	}
	
	private void update(float delta) {
		
	}
	
	private static final float FRAMES = 60.0f;
	private static final float NANOS_PER_FRAME = 1E9f / FRAMES;
	
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
			render(delta);
			frames++;
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				updates = 0;
				frames = 0;
			}
		}
	}
}
