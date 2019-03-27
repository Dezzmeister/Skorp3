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

public class Renderer2 {
	private final FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
	
	private int vaoID;
	private int vboID;
	private int uvBuffer;
	private int programID;
	private int mvpMatrixLocation;
	private int textureSamplerLocation;
	private volatile Mat4 mvpMatrix;
	
	private float[] vertices;
	private float[] uvVertices;
	private Triangle[] triangles;
	private Texture[] textures;
	private int[] textureIDs;
	private volatile boolean updateTriangles = false;
	
	public Renderer2(final String vertShaderPath, final String fragShaderPath) {		
		vaoID = createAndBindVAO();
		vboID = createVBO();
		uvBuffer = createUVBuffer();
		textures = createTextureArray();
		
		programID = createGLProgram(vertShaderPath, fragShaderPath);
		getShaderInputs();
	}
	
	public void render() {
		checkForUpdates();
		clearScreen();
		GL33.glUseProgram(programID);
		sendMVPMatrix();
		enableTriangles();
		enableUVVertices();
		drawTriangles();
		disableVertexAttribArrays(1);
	}
	
	private void drawTriangles() {
		for (int i = 0; i < triangles.length; i++) {
			Triangle t = triangles[i];
			int id = t.tex.glTextureID;
			int unit = t.tex.glImageUnit;
			
			GL33.glActiveTexture(GL33.GL_TEXTURE0 + unit);
			GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureSamplerLocation);
			GL33.glUniform1i(id, 0);
			GL33.glDrawArrays(GL33.GL_TRIANGLES, i * 3, 3);
		}
	}
	
	private int createUVBuffer() {
		int id;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer buf = stack.mallocInt(1);
			
			GL33.glGenBuffers(buf);
			id = buf.get(0);
		}
		
		return id;
	}
	
	private Texture[] createTextureArray() {
		int maxTextures = GL33.glGetInteger(GL33.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
		return new Texture[maxTextures];
	}
	
	private void disableVertexAttribArrays(int upto) {
		for (int i = 0; i <= upto; i++) {
			GL33.glDisableVertexAttribArray(upto);
		}
	}
	
	private void enableTriangles() {
		GL33.glEnableVertexAttribArray(0);
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboID);
		GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
	}
	
	private void enableUVVertices() {
		GL33.glEnableVertexAttribArray(1);
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureSamplerLocation);
		GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 0, 0);
	}
	
	private void sendMVPMatrix() {
		mvpMatrix.store(mvpBuffer);
		mvpBuffer.flip();
		GL33.glUniformMatrix4fv(mvpMatrixLocation, true, mvpBuffer);
	}
	
	private void clearScreen() {
		GL33.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL33.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	private void checkForUpdates() {
		if (updateTriangles) {
			sendTriangleInfo();
			updateTriangles = false;
		}
	}
	
	private void sendUVData(final float[] uvVerts) {
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, uvBuffer);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, uvVerts, GL33.GL_STATIC_DRAW);
	}
	
	public void sendTriangles(final Triangle[] _triangles) {
		triangles = _triangles;
		updateTriangles = true;
	}
	
	public void sendTrianglesAndWait(final Triangle[] _triangles) {
		sendTriangles(_triangles);
		
		while (updateTriangles);
	}
	
	private void sendTriangleInfo() {
		List<Texture> knownTextures = new ArrayList<Texture>();
		
		textureIDs = new int[triangles.length];
		vertices = new float[triangles.length * 9];
		uvVertices = new float[triangles.length * 6];
		
		int freeImageUnit = findNewImageUnit();
		
		if (freeImageUnit == -1) {
			Logger.error("Unable to send triangle info, no free texture units available.");
			return;
		}
		
		for (int i = 0; i < triangles.length; i++) {
			Triangle t = triangles[i];
			Texture tex = t.tex;
			
			int offset = i * 9;
			vertices[offset] = t.v0.x;
			vertices[offset + 1] = t.v0.y;
			vertices[offset + 2] = t.v0.z;
			
			vertices[offset + 3] = t.v1.x;
			vertices[offset + 4] = t.v1.y;
			vertices[offset + 5] = t.v1.z;
			
			vertices[offset + 6] = t.v2.x;
			vertices[offset + 7] = t.v2.y;
			vertices[offset + 8] = t.v2.z;
			
			offset = i * 6;
			uvVertices[offset] = t.uv0.x;
			uvVertices[offset + 1] = t.uv0.y;
			
			uvVertices[offset + 2] = t.uv1.x;
			uvVertices[offset + 3] = t.uv1.y;
			
			uvVertices[offset + 4] = t.uv2.x;
			uvVertices[offset + 5] = t.uv2.y;
			
			int id;
			
			if (!knownTextures.contains(tex)) {
				id = addTexture(tex);
				tex.glTextureID = id;
				
				tex.setImageUnit(freeImageUnit);
				textures[freeImageUnit] = tex;
				
				freeImageUnit = findNewImageUnit();
				
				if (freeImageUnit == -1) {
					textureIDs[i] = id;
					Logger.error("Unable to send triangle info, no free texture units available.");
					return;
				}
			} else {
				id = tex.glTextureID;
			}
			textureIDs[i] = id;
		}
		
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboID);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices, GL33.GL_STATIC_DRAW);
		
		sendUVData(uvVertices);
	}
	
	private int findNewImageUnit() {
		for (int i = 0; i < textures.length; i++) {
			if (textures[i] == null) {
				return i;
			}
		}
		
		return -1;
	}
	
	private int addTexture(final Texture tex) {
		int id = -1;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer buf = stack.mallocInt(1);
			
			GL33.glGenTextures(buf);
			id = buf.get(0);
		}
		
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, id);
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, tex.WIDTH, tex.HEIGHT, 0, GL33.GL_RGB, GL33.GL_UNSIGNED_INT, tex.pixels);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		
		return id;
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
	
	private void getShaderInputs() {
		mvpMatrixLocation = GL33.glGetUniformLocation(programID, "MVP");
		textureSamplerLocation = GL33.glGetUniformLocation(programID, "textureSampler");
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
	
	/**
	 * Sets the Model-View-Projection matrix for this renderer's OpenGL program. The new matrix will be loaded
	 * into the program in the next rendering cycle.
	 * 
	 * @param _mvpMatrix Model-View-Projection matrix
	 */
	public void setMVPMatrix(final Mat4 _mvpMatrix) {
		mvpMatrix = _mvpMatrix;
	}
}
