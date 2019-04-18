package com.dezzy.skorp3.game.graphics.rendering.shader;


public class ShaderPair {
	private final String vertexShaderPath;
	private final String fragmentShaderPath;
	
	public ShaderPair(final String _vertexShaderPath, final String _fragmentShaderPath) {
		vertexShaderPath = _vertexShaderPath;
		fragmentShaderPath = _fragmentShaderPath;
	}
}
