package com.dezzy.skorp3.game.graphics.utils;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import com.dezzy.skorp3.logging.Logger;

public class ShaderUtils {
	
	static {
		System.getProperty("line.separator");
	}
	
	public static int loadShader(final String path, int shaderType) throws IOException {
		int shaderID = GL33.glCreateShader(shaderType);
		
		String[] lines = Files.readAllLines(Paths.get(path)).stream().map(l -> l + System.lineSeparator()).collect(Collectors.toList()).toArray(new String[0]);
		
		GL33.glShaderSource(shaderID, lines);
		GL33.glCompileShader(shaderID);
		
		int infoLogLength;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer compileResultPtr = stack.mallocInt(1);
			IntBuffer infoLogLengthPtr = stack.mallocInt(1);
			
			GL33.glGetShaderiv(shaderID, GL33.GL_COMPILE_STATUS, compileResultPtr);
			GL33.glGetShaderiv(shaderID, GL33.GL_INFO_LOG_LENGTH, infoLogLengthPtr);
			
			infoLogLength = infoLogLengthPtr.get(0);
		}
		
		Logger.log("Shader \"" + path + "\" compiled: " + yesno(GL33.glGetShaderi(shaderID, GL33.GL_COMPILE_STATUS)));
		
		if (infoLogLength > 0) {
			String infoLog = GL33.glGetShaderInfoLog(shaderID);
			Logger.error("Error compiling shader \"" + path + "\": " + infoLog);
			throw new RuntimeException("Error compiling shader");
		}
		
		return shaderID;
	}
	
	public static int createProgramAndLinkShaders(int ... shaderIDs) {
		int programID = GL33.glCreateProgram();
		
		Arrays.stream(shaderIDs).forEach(shaderID -> GL33.glAttachShader(programID, shaderID));
		GL33.glLinkProgram(programID);
		Logger.log("OpenGL program linked: " + yesno(GL33.glGetProgrami(programID, GL33.GL_LINK_STATUS)));
		
		GL33.glValidateProgram(programID);
		Logger.log("OpenGL program validated: " + yesno(GL33.glGetProgrami(programID, GL33.GL_VALIDATE_STATUS)));
		
		int infoLogLength;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer resultPtr = stack.mallocInt(1);
			IntBuffer infoLogLengthPtr = stack.mallocInt(1);
			
			GL33.glGetProgramiv(programID, GL33.GL_LINK_STATUS, resultPtr);
			GL33.glGetProgramiv(programID, GL33.GL_INFO_LOG_LENGTH, infoLogLengthPtr);
			
			infoLogLength = infoLogLengthPtr.get(0);
		}
		
		if (infoLogLength > 0) {
			String infoLog = GL33.glGetProgramInfoLog(programID);
			Logger.error("Error creating OpenGL program: " + infoLog);
			throw new RuntimeException("Error creating OpenGL program");
		}
		
		Arrays.stream(shaderIDs).forEach(shaderID -> {
			GL33.glDetachShader(programID, shaderID);
			GL33.glDeleteShader(shaderID);
		});
		
		return programID;
	}
	
	private static String yesno(int i) {
		return (i == 1) ? "YES" : "NO";
	}
}
