package com.dezzy.skorp3.game.graphics.rendering;

import com.dezzy.skorp3.messaging.MessageHandler;

public class GL33Renderer {
	/**
	 * Internal event stream to facilitate communication between caller/control threads and the renderer thread.
	 * OpenGL only allows one thread to make GL calls for a context, so any external calls must be piped through
	 * one thread.
	 */
	private final MessageHandler internalPipe;
	/**
	 * Path to the vertex shader, which must be written in GLSL version 3.3
	 */
	private final String vertShaderPath;
	/**
	 * Path to the fragment shader, which must be written in GLSL version 3.3
	 */
	private final String fragShaderPath;
	
	public GL33Renderer(final String _vertShaderPath, final String _fragShaderPath) {
		internalPipe = MessageHandler.createHandler("internal");
		
		vertShaderPath = _vertShaderPath;
		fragShaderPath = _fragShaderPath;
	}
	
	/**
	 * Pointers to uniform members in the vertex and fragment shaders.
	 *
	 * @author Joe Desmond
	 */
	private class Uniforms {
		
	}
}
