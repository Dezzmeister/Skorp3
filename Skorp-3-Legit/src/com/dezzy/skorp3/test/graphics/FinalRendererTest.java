package com.dezzy.skorp3.test.graphics;

import java.awt.Dimension;
import java.awt.Point;

import com.dezzy.skorp3.game.Game;
import com.dezzy.skorp3.game.GameStarter;
import com.dezzy.skorp3.game.actors.Player;
import com.dezzy.skorp3.game.graphics.Renderer;
import com.dezzy.skorp3.game.graphics.Texture;
import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.graphics.geometry.composite.Block;
import com.dezzy.skorp3.game.graphics.geometry.composite.Mesh;
import com.dezzy.skorp3.game.graphics.geometry.composite.OBJModel;
import com.dezzy.skorp3.game.graphics.utils.TransformUtils;
import com.dezzy.skorp3.game.input.KeyboardHandler;
import com.dezzy.skorp3.game.input.MouseHandler;
import com.dezzy.skorp3.game.math.Mat4;
import com.dezzy.skorp3.game.math.Vec4;

public class FinalRendererTest {
	
	public static void testTexturedGameWithInput() {
		int WIDTH = 750;
		int HEIGHT = 750;
		
		GameStarter skorp = new GameStarter(new Dimension(WIDTH, HEIGHT), false);
		Thread gameThread = new Thread(skorp);
		gameThread.start();
		
		skorp.waitForInitialization();
		
		Player mainPlayer = new Player(new Vec4(0, 0, 0));
		boolean[] keys = skorp.keyHandler.keys;
		float speed = 0.06f;
		float lookSpeed = 0.06f;
		
		skorp.game.injectUpdater(d -> {
			if (keys['W']) {
				mainPlayer.moveForward(-speed);
			}
			
			if (keys['S']) {
				mainPlayer.moveBackward(-speed);
			}
			
			if (keys['A']) {
				mainPlayer.moveRight(speed);
			}
			
			if (keys['D']) {
				mainPlayer.moveLeft(speed);
			}
			
			//Left arrow
			if (keys[263]) {
				mainPlayer.lookAround(lookSpeed);
			}
			
			//Right arrow
			if (keys[262]) {
				mainPlayer.lookAround(-lookSpeed);
			}
		});
		
		//Triangle triangle = new Triangle(new Vec4(-1, -1, 0), new Vec4(1, -1, 0), new Vec4(0, 1, 0));
		Texture wall512 = new Texture("assets/textures/wall512.png");
		//Texture tabletop128 = new Texture("assets/textures/tabletop128.png");
		/*
		Square tableSquare = new Square().setTexture(tabletop128);
		Square wallSquare = new Square().setTexture(wall512);
		
		Mat4 transformer = TransformUtils.rotateY(3.14159f/2.0f).multiply(TransformUtils.translate(0.5f, 0, 0.5f));
		transformer.transform(wallSquare);
		
		
		triangle.setTexture(wall512);
		triangle.setUV(new Vec2(0, 1), new Vec2(1, 1), new Vec2(0.5f, 0));
		
		//skorp.renderer2.sendTrianglesAndWait(new Triangle[] {triangle});
		Mesh cube = new OBJModel("assets/models/cube/cube.obj");
		TransformUtils.scale(0.01f, 0.01f, 0.01f).transform(cube);
		
		//skorp.renderer2.sendTrianglesAndWait(wallSquare.add(tableSquare).triangles);
		Block block = new Block(tabletop128);
		TransformUtils.translate(0, -1, 2).transform(block);
		
		Block block2 = new Block(wall512);
		TransformUtils.translate(0, 0, 3).transform(block2);
		//skorp.renderer2.sendTrianglesAndWait(block.add(block2).triangles);
		*/
		Block joj = new Block(wall512);
		TransformUtils.translate(0, 0, -4).transform(joj);
		
		joj.resolveNormals();	
		
		skorp.renderer2.sendTrianglesAndWait(joj);
		//Mesh donut = new OBJModel("assets/models/donut/donut.obj");
		//TransformUtils.scale(0.01f, 0.01f, 0.01f).transform(donut);
		
		//skorp.renderer2.sendTrianglesAndWait(donut.triangles);
		//skorp.renderer2.sendTrianglesAndWait(cube.triangles);
		skorp.renderer2.enableRendering();
		
		Mat4 proj = TransformUtils.perspective((float)Math.PI / 4.0f, WIDTH/(float)HEIGHT, 0.1f, 100.0f);
		
		Vec4 pos = mainPlayer.position;
		while (skorp.game.isRunning()) {
			Mat4 view = TransformUtils.rotateY(-mainPlayer.yaw).multiply(TransformUtils.translate(-pos.x, -pos.y, -pos.z));
			
			Mat4 mvpMatrix = proj.multiply(view);
			skorp.renderer2.setMVPMatrix(mvpMatrix);
		}
	}
	
	public static void testGameWithInput() {
		int WIDTH = 750;
		int HEIGHT = 750;
		
		GameStarter skorp = new GameStarter(new Dimension(WIDTH, HEIGHT), false);
		Thread gameThread = new Thread(skorp);
		gameThread.start();
		
		skorp.waitForInitialization();
		
		Player mainPlayer = new Player(new Vec4(0, 0, 0));
		boolean[] keys = skorp.keyHandler.keys;
		float speed = 0.06f;
		float lookSpeed = 0.06f;
		
		skorp.game.injectUpdater(d -> {
			if (keys['W']) {
				mainPlayer.moveForward(-speed);
			}
			
			if (keys['S']) {
				mainPlayer.moveBackward(-speed);
			}
			
			if (keys['A']) {
				mainPlayer.moveRight(speed);
			}
			
			if (keys['D']) {
				mainPlayer.moveLeft(speed);
			}
			
			//Left arrow
			if (keys[263]) {
				mainPlayer.lookAround(lookSpeed);
			}
			
			//Right arrow
			if (keys[262]) {
				mainPlayer.lookAround(-lookSpeed);
			}
		});
		
		Mesh mesh = new Mesh(new Triangle(
				new Vec4(-1, -1, 0),
				new Vec4(1, -1, 0),
				new Vec4(0, 1, 0)
			).setColors(
				new Vec4(1, 0, 0),
				new Vec4(0, 1, 0),
				new Vec4(0, 0, 1)
			),
			new Triangle(
				new Vec4(0, 1, 0),
				new Vec4(1, -1, 0),
				new Vec4(1, -1, -2)
			).setColors(
				new Vec4(1, 1, 0),
				new Vec4(0, 1, 1),
				new Vec4(1, 0, 1)
			),
			new Triangle(
				new Vec4(1, -1, 0),
				new Vec4(1, -1, -2),
				new Vec4(-1, -1, 0)
			).setColors(
				new Vec4(1, 1, 1),
				new Vec4(0, 0, 0),
				new Vec4(0.5f, 0.5f, 0.5f)
			),
			new Triangle(
				new Vec4(1, -1, -2),
				new Vec4(-1, -1, 0),
				new Vec4(-1, -1, -2)
			).setColors(
				new Vec4(0, 0, 0),
				new Vec4(0.5f, 0.5f, 0.5f),
				new Vec4(0, 0, 0)
			)
		);
		
		skorp.renderer.setVBOVertices(mesh.getVBOVertices());
		skorp.renderer.setColors(mesh.getColors());
		
		Mat4 proj = TransformUtils.perspective((float)Math.PI / 4.0f, WIDTH/(float)HEIGHT, 0.1f, 100.0f);
		
		Vec4 pos = mainPlayer.position;
		while (skorp.game.isRunning()) {
			//System.out.println(mainPlayer.position);
			Mat4 model = TransformUtils.rotateY(-mainPlayer.yaw).multiply(TransformUtils.translate(-pos.x, -pos.y, -pos.z));
			
			Mat4 mvpMatrix = proj.multiply(model);
			skorp.renderer.setMVPMatrix(mvpMatrix);
		}
	}
	
	public static void testMultithreadedGame() {
		int WIDTH = 500;
		int HEIGHT = 500;
		
		GameStarter gameStarter = new GameStarter(new Dimension(WIDTH, HEIGHT), false);
		Thread gameThread = new Thread(gameStarter);
		gameThread.start();
		
		gameStarter.waitForInitialization();
		
		FloatPtr anglePtr = new FloatPtr(0);
		
		gameStarter.game.injectUpdater(d -> anglePtr.set((anglePtr.get() + (0.05f)) % ((float)Math.PI * 2)));
		
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
			Mat4 model = TransformUtils.translate(0, 0, -4).multiply(TransformUtils.rotateY(anglePtr.get()));
			
			Mat4 mvpMatrix = proj.multiply(model);
			
			gameStarter.renderer.setMVPMatrix(mvpMatrix);
		}
	}
	
	public static void testGame() {
		int WIDTH = 500;
		int HEIGHT = 500;
		
		MouseHandler mouseHandler = new MouseHandler(new Point(0,0), new Point(960, 540));
		KeyboardHandler keyHandler = new KeyboardHandler();
		
		Game game = new Game(keyHandler, mouseHandler, new Dimension(WIDTH, HEIGHT), false);
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
