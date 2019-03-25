package com.dezzy.skorp3.game;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import com.dezzy.skorp3.game.graphics.Renderer;
import com.dezzy.skorp3.game.input.KeyboardHandler;
import com.dezzy.skorp3.game.input.MouseHandler;
import com.dezzy.skorp3.game.math.Mat4;
import com.dezzy.skorp3.logging.Logger;

/**
 * Manages the creation of a Skorp 3 game and all of its objects. It is necessary that the parts of the game run on their own thread,
 * because OpenGL only permits GL function calls for a context from the thread that created a context.
 *
 * @author Joe Desmond
 */
public class GameStarter implements Runnable {
	private volatile boolean initFinished = false;
	
	public Game game;
	public Renderer renderer;
	public MouseHandler mouseHandler;
	public KeyboardHandler keyHandler;
	private Dimension initialWindowSize;
	
	public GameStarter(final Dimension _initialWindowSize) {
		initialWindowSize = _initialWindowSize;
	}
	
	public boolean initializationFinished() {
		return initFinished;
	}
	
	/**
	 * Waits for this GameStarter to finish initializing all local members on its thread.
	 * When this GameStarter starts a game on its own thread, it first initializes the Game, Renderer,
	 * and other game objects. If the original thread does not wait for this initialization to complete,
	 * null pointer exceptions can occur if something tries to access a member too soon.
	 */
	public void waitForInitialization() {
		while (!initFinished);
	}
	
	private void init() {
		DisplayMode screenRes = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		Logger.log("Running on monitor with resolution " + screenRes.getWidth() + "x" + screenRes.getHeight());
		
		int width = initialWindowSize.width;
		int height = initialWindowSize.height;
		
		mouseHandler = new MouseHandler(new Point(0,0), new Point(screenRes.getWidth() / 2, screenRes.getHeight() / 2));
		keyHandler = new KeyboardHandler();
		
		game = new Game(keyHandler, mouseHandler, new Dimension(width, height));
		renderer = new Renderer("shaders/vert0.glsl", "shaders/frag0.glsl");
		
		game.setRenderer(renderer);
		
		renderer.setVBOVertices(new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0});
		renderer.setColors(new float[] {1, 1, 1, 1, 1, 1, 1, 1, 1});
		
		renderer.setMVPMatrix(Mat4.IDENTITY);
		
		initFinished = true;
	}
	
	@Override
	public void run() {
		init();
		game.startAndRun();
	}
}
