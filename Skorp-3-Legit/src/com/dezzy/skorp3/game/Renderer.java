package com.dezzy.skorp3.game;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL43;

public class Renderer {
	
	private int createVBOID() {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL43.glGenBuffers(buffer);
		return buffer.get(0);
	}
}
