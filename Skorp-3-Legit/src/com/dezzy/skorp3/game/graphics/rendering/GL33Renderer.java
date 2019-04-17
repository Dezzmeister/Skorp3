package com.dezzy.skorp3.game.graphics.rendering;

import com.dezzy.skorp3.game.graphics.geometry.composite.Mesh;
import com.dezzy.skorp3.messaging.Message;
import com.dezzy.skorp3.messaging.MessageHandler;
import com.dezzy.skorp3.messaging.meta.Sends;
import com.dezzy.skorp3.messaging.meta.SendsTo;

public class GL33Renderer {
	/**
	 * Internal event stream to facilitate communication between caller/control threads and the renderer thread.
	 * OpenGL only allows one thread to make GL calls for a context, so any external calls must be piped through
	 * one thread.
	 */
	private final MessageHandler internalPipe;
	private static final String INTERNAL_PIPE_NAME = GL33Renderer.class.getName() + " Internal Message Pipe";
	/**
	 * Path to the vertex shader, which must be written in GLSL version 3.3
	 */
	private final String vertShaderPath;
	/**
	 * Path to the fragment shader, which must be written in GLSL version 3.3
	 */
	private final String fragShaderPath;
	
	public GL33Renderer(final String _vertShaderPath, final String _fragShaderPath) {
		internalPipe = MessageHandler.createHandler(INTERNAL_PIPE_NAME);
		
		vertShaderPath = _vertShaderPath;
		fragShaderPath = _fragShaderPath;
	}
	
	@Sends("setWorldMesh")
	@SendsTo("com.dezzy.skorp3.game.graphics.rendering.GL33Renderer Internal Message Pipe")
	public void setWorldMesh(final Mesh mesh) {
		Message meshMessage = new Message("setWorldMesh", mesh);
		internalPipe.dispatch(meshMessage);
	}
	
	/**
	 * Pointers to uniform members in the vertex and fragment shaders.
	 *
	 * @author Joe Desmond
	 */
	private class Uniforms {
		
	}
}
