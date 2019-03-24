package com.dezzy.skorp3.game.graphics;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import com.dezzy.skorp3.game.graphics.utils.ShaderUtils;
import com.dezzy.skorp3.game.math.Mat4;

public class Renderer {
	private final FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
	
	private volatile Mat4 mvpMatrix;
	
	private volatile float[] vertices;
	private volatile boolean vertsUpdated = false;
	
	private volatile float[] colors;
	private volatile boolean colorsUpdated = false;
	
	private final ShaderData vertShaderData = new ShaderData();
	private int programID;
	private int vaoID;
	private int vboID;
	private int colorBuffer;
	
	public Renderer(final String vertShaderPath, final String fragShaderPath) {		
		vaoID = createAndBindVAO();
		vboID = createVBO();
		colorBuffer = createColorBuffer();
		
		programID = createGLProgram(vertShaderPath, fragShaderPath);
		getShaderInputs();
	}
	
	public void render() {
		checkVBOVerticesUpdates();
		checkColorUpdates();
		
		clearScreen();		
		GL33.glUseProgram(programID);
		sendMVPMatrix();
		
		sendTriangles();
		sendColors();
		drawTriangles();
		disableVertexAttribArrays(1);
	}
	
	private void checkVBOVerticesUpdates() {
		if (vertsUpdated) {
			sendVBOData();
			vertsUpdated = false;
		}
	}
	
	private void checkColorUpdates() {
		if (colorsUpdated) {
			sendColorData();
			colorsUpdated = false;
		}
	}
	
	private void disableVertexAttribArrays(int upto) {
		for (int i = 0; i <= upto; i++) {
			GL33.glDisableVertexAttribArray(upto);
		}
	}
	
	private void sendColors() {
		GL33.glEnableVertexAttribArray(1);
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, colorBuffer);
		GL33.glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 0, 0);
	}
	
	private void sendTriangles() {
		GL33.glEnableVertexAttribArray(0);
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboID);
		GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
	}
	
	private void drawTriangles() {
		GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vertices.length / 3);
	}
	
	private void clearScreen() {
		GL33.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL33.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	private void sendMVPMatrix() {
		mvpMatrix.store(mvpBuffer);
		mvpBuffer.flip();
		GL33.glUniformMatrix4fv(vertShaderData.mvpMatrixLocation, false, mvpBuffer);
	}
	
	private void getShaderInputs() {
		vertShaderData.mvpMatrixLocation = GL33.glGetUniformLocation(programID, "MVP");
	}
	
	private int createGLProgram(final String vertShaderPath, final String fragShaderPath) {
		int _programID = -1;
		
		try {
			int vertShader = ShaderUtils.loadShader(vertShaderPath, GL33.GL_VERTEX_SHADER);
			int fragShader = ShaderUtils.loadShader(fragShaderPath, GL33.GL_FRAGMENT_SHADER);
			
			_programID = ShaderUtils.createProgramAndLinkShaders(vertShader, fragShader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return _programID;
	}
	
	private int createAndBindVAO() {
		int _vaoID = -1;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer id = stack.mallocInt(1);
			GL33.glGenVertexArrays(id);
			_vaoID = id.get(0);
		}
		GL33.glBindVertexArray(_vaoID);
		
		return _vaoID;
	}
	
	private int createVBO() {
		int _vboID = -1;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer id = stack.mallocInt(1);
			
			GL33.glGenBuffers(id);
			_vboID = id.get(0);
		}
		
		return _vboID;
	}
	
	private int createColorBuffer() {
		int _colorBuf = -1;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer id = stack.mallocInt(1);
			
			GL33.glGenBuffers(id);
			_colorBuf = id.get(0);
		}
		
		return _colorBuf;
	}
	
	public void setVBOVertices(final float[] _vertices) {
		vertices = _vertices;
		vertsUpdated = true;
	}
	
	public void setColors(final float[] _colors) {
		colors = _colors;
		colorsUpdated = true;
	}
	
	private void sendVBOData() {
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboID);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices, GL33.GL_STATIC_DRAW);
	}
	
	private void sendColorData() {
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, colorBuffer);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, colors, GL33.GL_STATIC_DRAW);
	}
	
	public void setMVPMatrix(final Mat4 _mvpMatrix) {
		mvpMatrix = _mvpMatrix;
	}
	
	private class ShaderData {
		private int mvpMatrixLocation = -1;
	}
}
