package com.dezzy.skorp3.game.graphics.rendering.shader;

import java.io.IOException;

import org.lwjgl.opengl.GL33;

import com.dezzy.skorp3.game.graphics.utils.ShaderUtils;

public class ShaderPair {
	private final String vertexShaderPath;
	private final String fragmentShaderPath;
	private final ShaderCapability[] capabilities;
	
	public ShaderPair(final String _vertexShaderPath, final String _fragmentShaderPath, final ShaderCapability ... _capabilities) {
		vertexShaderPath = _vertexShaderPath;
		fragmentShaderPath = _fragmentShaderPath;
		capabilities = _capabilities;
	}
	
	public boolean isCapableOf(final ShaderCapability capability) {
		for (ShaderCapability cap : capabilities) {
			if (cap == capability) {
				return true;
			}
		}
		return false;
	}
	
	public int loadVertexShader() throws IOException {
		return ShaderUtils.loadShader(vertexShaderPath, GL33.GL_VERTEX_SHADER);
	}
	
	public int loadFragmentShader() throws IOException {
		return ShaderUtils.loadShader(fragmentShaderPath, GL33.GL_FRAGMENT_SHADER);
	}
}
