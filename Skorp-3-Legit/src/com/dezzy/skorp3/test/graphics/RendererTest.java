package com.dezzy.skorp3.test.graphics;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

public class RendererTest {
	private int vaoID;
	private int vboID;
	float[] vertices;
	
	public RendererTest() {
		vaoID = createVAOID();
		bindVAO(vaoID);
		
		vboID = createVBOID();
		bindVBOBuffer(vboID);
		vertices = new float[] {
				-1.0f, -1.0f, 0.0f,
				1.0f, -1.0f, 0.0f,
				0.0f, 1.0f, 0.0f
		};
	}
	
	public void render() {
		GL33.glEnableVertexAttribArray(0);
		sendVBOData(vertices);
		GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
		
		GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 3);
		GL33.glDisableVertexAttribArray(0);
	}
	
	private int createVAOID() {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL33.glGenVertexArrays(buffer);
		
		return buffer.get(0);
	}
	
	private void bindVAO(int vaoID) {
		GL33.glBindVertexArray(vaoID);
	}
	
	private int createVBOID() {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL33.glGenBuffers(buffer);
		return buffer.get(0);
	}
	
	private void bindVBOBuffer(int vboID) {
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboID);
	}
	
	private void sendVBOData(float[] vertices) {
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices, GL33.GL_STATIC_DRAW);
	}
}
