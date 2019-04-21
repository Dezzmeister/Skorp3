package com.dezzy.skorp3.game.graphics.rendering.shader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL33;

import com.dezzy.skorp3.game.graphics.utils.ShaderUtils;
import com.dezzy.skorp3.logging.Logger;

public class ShaderPair {
	private final String vertexShaderPath;
	private final String fragmentShaderPath;
	private final ShaderCapability[] capabilities;
	private final Map<ShaderSubCapability, Location> locations = new HashMap<ShaderSubCapability, Location>();
	
	public ShaderPair(final String _vertexShaderPath, final String _fragmentShaderPath, final ShaderCapability ... _capabilities) {
		vertexShaderPath = _vertexShaderPath;
		fragmentShaderPath = _fragmentShaderPath;
		capabilities = _capabilities;
	}
	
	public void getFieldPointers(int glProgramID) {
		for (Location location : locations.values()) {
			location.setGLLocation(glProgramID);
		}
	}
	
	public boolean isCapableOf(final ShaderCapability capability) {
		for (ShaderCapability cap : capabilities) {
			if (cap == capability) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCapableOf(final ShaderSubCapability subCapability) {
		for (ShaderCapability capability : capabilities) {
			for (ShaderSubCapability subCap : capability.subCapabilities) {
				if (subCap == subCapability) {
					return true;
				}
			}
		}
		
		return false;
	}	
	
	public void setLocationOf(final ShaderSubCapability subCapability, final Location location) {
		if (!isCapableOf(subCapability)) {
			Logger.error(toString() + " does not have sub-capability " + subCapability.name());
		}
		
		locations.put(subCapability, location);
	}
	
	public int loadVertexShader() throws IOException {
		return ShaderUtils.loadShader(vertexShaderPath, GL33.GL_VERTEX_SHADER);
	}
	
	public int loadFragmentShader() throws IOException {
		return ShaderUtils.loadShader(fragmentShaderPath, GL33.GL_FRAGMENT_SHADER);
	}
	
	@Override
	public String toString() {
		return "ShaderPair defined by (" + vertexShaderPath + ", " + fragmentShaderPath + ")";
	}
	
	/**
	 * Represents the location of an attribute or uniform in a shader. For attributes the location is defined by a number, and for uniforms
	 * it is defined by a name.
	 *
	 * @author Joe Desmond
	 */
	public class Location {
		/**
		 * Name of a uniform object in the shader
		 */
		private final String name;
		/**
		 * Location as specified by 'layout' (for vertex attributes)
		 */
		private final int location;
		/**
		 * GL pointer to the field in the shader. If the field is a uniform, this will be set by <code>glGetUniformLocation()</code>;
		 * otherwise, it will be equal to the numerical location in the shader (the layout value).
		 */
		private int glLocation;
		
		/**
		 * Creates a Location representing a uniform in a shader.
		 * 
		 * @param _name uniform name
		 */
		public Location(final String _name) {
			name = _name;
			location = -1;
		}
		
		/**
		 * Creates a Location representing an attribute in a shader.
		 * 
		 * @param _location location of the attribute
		 */
		public Location(int _location) {
			location = _location;
			name = "";
		}
		
		/**
		 * Returns true if this Location is defined as a uniform, i.e., if it was created with a name instead of a number.
		 * 
		 * @return true if this represents a uniform
		 */
		public boolean isUniform() {
			return location == -1;
		}
		
		private void setGLLocation(int glProgramID) {
			if (isUniform()) {
				glLocation = GL33.glGetUniformLocation(glProgramID, name);
			} else {
				glLocation = location;
			}
		}
	}
}
