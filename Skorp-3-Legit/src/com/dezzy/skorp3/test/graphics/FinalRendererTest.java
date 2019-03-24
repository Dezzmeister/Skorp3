package com.dezzy.skorp3.test.graphics;

import java.awt.Dimension;
import java.awt.Point;

import com.dezzy.skorp3.game.Game;
import com.dezzy.skorp3.game.GameStarter;
import com.dezzy.skorp3.game.graphics.Renderer;
import com.dezzy.skorp3.game.graphics.geometry.Mesh;
import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.graphics.utils.TransformUtils;
import com.dezzy.skorp3.game.input.KeyboardHandler;
import com.dezzy.skorp3.game.input.MouseHandler;
import com.dezzy.skorp3.game.math.Mat4;
import com.dezzy.skorp3.game.math.Vec4;

public class FinalRendererTest {
	
	public static void testMultithreadedGame() {
		int WIDTH = 500;
		int HEIGHT = 500;
		
		GameStarter gameStarter = new GameStarter(new Dimension(WIDTH, HEIGHT));
		Thread gameThread = new Thread(gameStarter);
		gameThread.start();
		
		gameStarter.waitForInitialization();
		
		FloatPtr anglePtr = new FloatPtr(0);
		
		gameStarter.game.injectUpdater(d -> anglePtr.set((anglePtr.get() + (0.01f)) % ((float)Math.PI * 2)));
		
		Mesh mesh = new Mesh(new Triangle(
				new Vec4(-1, -1, 0),
				new Vec4(1, -1, 0),
				new Vec4(0, 1, 0)
		).setColors(
				new Vec4(1, 0, 0),
				new Vec4(0, 1, 0),
				new Vec4(0, 0, 1)
		));
		
		gameStarter.renderer.setVBOVertices(mesh.getVBOVertices());
		gameStarter.renderer.setColors(mesh.getColors());
		
		Mat4 proj = TransformUtils.perspective((float)Math.PI / 4.0f, WIDTH/(float)HEIGHT, 0.1f, 100.0f);
		
		while (gameStarter.game.isRunning()) {
			Mat4 model = TransformUtils.translate(0, 0, -8).multiply(TransformUtils.rotateY(anglePtr.get()));
			
			Mat4 mvpMatrix = proj.multiply(model);
			
			gameStarter.renderer.setMVPMatrix(mvpMatrix);
		}
	}
	
	public static void testGame() {
		int WIDTH = 500;
		int HEIGHT = 500;
		
		MouseHandler mouseHandler = new MouseHandler(new Point(0,0), new Point(960, 540));
		KeyboardHandler keyHandler = new KeyboardHandler();
		
		Game game = new Game(keyHandler, mouseHandler, new Dimension(WIDTH, HEIGHT));
		Renderer renderer = new Renderer("shaders/vert0.glsl", "shaders/frag0.glsl");
		game.setRenderer(renderer);
		
		FloatPtr anglePtr = new FloatPtr(0);
		game.injectUpdater(f -> anglePtr.value += 0.02f);
		
		Mesh mesh = new Mesh(new Triangle(
				new Vec4(-1, -1, 0),
				new Vec4(1, -1, 0),
				new Vec4(0, 1, 0)
		));
		renderer.setVBOVertices(mesh.getVBOVertices());
		
		Mat4 proj = TransformUtils.perspective((float)Math.PI / 4.0f, WIDTH/(float)HEIGHT, 0.1f, 100.0f);
		Mat4 model = TransformUtils.translate(0, 0, -4).multiply(TransformUtils.rotateZ(anglePtr.get()));
		
		Mat4 mvpMatrix = proj.multiply(model);
		
		renderer.setMVPMatrix(mvpMatrix);
		
		game.startAndRun();
	}
	
	private static class FloatPtr {
		public volatile float value;
		
		public FloatPtr(float _value) {
			value = _value;
		}
		
		public void add(float inc) {
			value += inc;
		}
		
		public synchronized void set(float val) {
			value = val;
		}
		
		public float get() {
			return value;
		}
	}
}
