package com.dezzy.skorp3.game.graphics;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.graphics.utils.ShaderUtils;
import com.dezzy.skorp3.game.math.Mat4;
import com.dezzy.skorp3.logging.Logger;

/**
 * Draws to the screen using OpenGL. This class is intended to be thread-safe. Because of OpenGL rules about
 * calling threads, this should be created and run on its own thread. Proxy methods are provided to pass data between
 * a main thread and this renderer. 
 *
 * @author Joe Desmond
 */
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
	private List<Texture> knownTextures = new ArrayList<Texture>();
	private volatile boolean texturesSent = false;
	
	/**
	 * Constructs a Renderer and an OpenGL program from the given shaders. The renderer
	 * has one VBO and one color buffer. <br>
	 * <b>NOTE:</b> Because this constructor uses OpenGL functions, 
	 * it should be called from the thread that created the OpenGL context.
	 * 
	 * @param vertShaderPath path to vertex shader
	 * @param fragShaderPath path to fragment shader
	 */
	public Renderer(final String vertShaderPath, final String fragShaderPath) {		
		vaoID = createAndBindVAO();
		vboID = createVBO();
		colorBuffer = createColorBuffer();
		
		programID = createGLProgram(vertShaderPath, fragShaderPath);
		getShaderInputs();
	}
	
	/**
	 * Renders this renderer's vertices and colors. <br>
	 * <b>NOTE:</b> Because this function uses OpenGL functions, 
	 * it should be called from the thread that created the OpenGL context.
	 */
	public void render() {
		checkVBOVerticesUpdates();
		checkColorUpdates();
		checkForNewTextures();
		
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
		GL33.glUniformMatrix4fv(vertShaderData.mvpMatrixLocation, true, mvpBuffer);
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

	private void checkForNewTextures() {
		if (!texturesSent) {
			for (int i = 0; i < knownTextures.size(); i++) {
				addTexture(knownTextures.get(i));
			}
		}
	}
	
	private int addTexture(final Texture tex) {
		int textureID = -1;
		
		for (int i = 0; i < knownTextures.size(); i++) {
			if (tex == knownTextures.get(i)) {
				return knownTextures.get(i).glTextureID;
			}
		}
		knownTextures.add(tex);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer id = stack.mallocInt(1);
			GL33.glGenTextures(id);
			textureID = id.get(0);
		}
		
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureID);
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, tex.WIDTH, tex.HEIGHT, 0, GL33.GL_RGB, GL33.GL_UNSIGNED_INT, tex.pixels);
		
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		
		tex.setTextureID(textureID);
		return textureID;
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
	
	/**
	 * Sets the vertex buffer for this renderer's OpenGL program. The new vertices will be loaded
	 * into the program at the start of the next rendering cycle.
	 * 
	 * @param _vertices triangle vertices
	 */
	public void setVBOVertices(final float[] _vertices) {
		vertices = _vertices;
		vertsUpdated = true;
	}
	
	/**
	 * Sets the color buffer for this renderer's OpenGL program. The new colors will be loaded
	 * into the program at the start of the next rendering cycle.
	 * 
	 * @param _colors RGB values for triangle vertices
	 */
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
	
	/**
	 * Sets the Model-View-Projection matrix for this renderer's OpenGL program. The new matrix will be loaded
	 * into the program in the next rendering cycle.
	 * 
	 * @param _mvpMatrix Model-View-Projection matrix
	 */
	public void setMVPMatrix(final Mat4 _mvpMatrix) {
		mvpMatrix = _mvpMatrix;
	}
	
	private class ShaderData {
		private int mvpMatrixLocation = -1;
	}
}
