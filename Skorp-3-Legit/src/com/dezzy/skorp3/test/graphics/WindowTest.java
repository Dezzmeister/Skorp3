package com.dezzy.skorp3.test.graphics;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
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
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import com.dezzy.skorp3.game.graphics.utils.ShaderUtils;
import com.dezzy.skorp3.game.graphics.utils.TransformUtils;
import com.dezzy.skorp3.game.math.Mat4;
import com.dezzy.skorp3.game.math.Vec4;
import com.dezzy.skorp3.logging.Logger;
import com.dezzy.skorp3.messaging.Message;
import com.dezzy.skorp3.messaging.MessageHandler;
import com.dezzy.skorp3.messaging.meta.Sends;
import com.dezzy.skorp3.messaging.meta.SendsTo;
import com.dezzy.skorp3.test.math.MatrixTest;

public class WindowTest implements Runnable {
	private long window;
	private int vaoID;
	private int vboID;
	
	@Override
	public void run() {
		Logger.log("Starting WindowTest");
		
		init();
		loop();
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	private void init() {		
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(Logger.getLogger()).set());
		
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW!");
		}
		
		glfwDefaultWindowHints();
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		//Use glfwGetPrimaryMonitor() for fullscreen
		window = glfwCreateWindow(300, 300, "Window Test", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("GLFW window could not be created");
		}
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true);
				Logger.log("Escape key was pressed");
			}
		});
		
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
		
		GLCapabilities capabilities = GL.createCapabilities();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer id = stack.mallocInt(1);
			GL33.glGenVertexArrays(id);
			vaoID = id.get(0);
		}
		GL33.glBindVertexArray(vaoID);
		
		Logger.log("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
	}
	
	private FloatBuffer matBuf = BufferUtils.createFloatBuffer(16);
	
	@Sends({"QUIT"})
	@SendsTo({"Global"})
	private void loop() {
		
		final float[] vertices = new float[] {
				-1.0f, -1.0f, 0.0f,
				1.0f, -1.0f, 0.0f,
				0.0f, 1.0f, 0.0f
		};
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer id = stack.mallocInt(1);
			
			GL33.glGenBuffers(id);
			vboID = id.get(0);
		}
		
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboID);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices, GL33.GL_STATIC_DRAW);
		
		int programID = 0;
		try {
			int vertShader = ShaderUtils.loadShader("shaders/vert0.glsl", GL33.GL_VERTEX_SHADER);
			int fragShader = ShaderUtils.loadShader("shaders/frag0.glsl", GL33.GL_FRAGMENT_SHADER);
			
			programID = ShaderUtils.createProgramAndLinkShaders(vertShader, fragShader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Mat4 projectionMatrix = TransformUtils.perspective((float)Math.PI / 4.0f, 1.0f, 0.01f, 100.0f);
		/*
		Mat4 viewMatrix = TransformUtils.lookAt(
				new Vec4(4, 3, 3, 1),
				new Vec4(0, 0, 0, 1),
				new Vec4(0, 1, 0, 1)
		);
		*/
		Mat4 viewMatrix = Mat4.IDENTITY;
		
		Mat4 modelMatrix = TransformUtils.rotateZ(0.4f).multiply(TransformUtils.translate(0, 0, -4f));
		
		int mvpMatrixLocation = GL33.glGetUniformLocation(programID, "MVP");
		
		Mat4 mvpMatrix = Mat4.IDENTITY;
		
		glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
		
		float factor = 0;
		
		while (!glfwWindowShouldClose(window)) {
			//modelMatrix = TransformUtils.rotateZ((factor += 0.01f));
			
			mvpMatrix = projectionMatrix.multiply(viewMatrix).multiply(modelMatrix);
			
			/*
			System.out.println("vec0: " + mvpMatrix.multiply(new Vec4(-1, -1, 0)));
			System.out.println("vec1: " + mvpMatrix.multiply(new Vec4(1, -1, 0)));
			System.out.println("vec2: " + mvpMatrix.multiply(new Vec4(0, 1, 0)));
			*/
			
			GL33.glUseProgram(programID);
			
			mvpMatrix.store(matBuf);
			matBuf.flip();
			GL33.glUniformMatrix4fv(mvpMatrixLocation, false, matBuf);
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
			GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 3);
			GL33.glDisableVertexAttribArray(0);
			
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
		Logger.log("Quitting GLFW");
		MessageHandler.dispatchGlobal(new Message("QUIT", null));
	}
}
