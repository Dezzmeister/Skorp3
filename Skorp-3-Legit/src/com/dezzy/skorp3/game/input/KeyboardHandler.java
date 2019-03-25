package com.dezzy.skorp3.game.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

public class KeyboardHandler implements GLFWKeyCallbackI {
	public final boolean[] keys = new boolean[512];
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (action == GLFW.GLFW_RELEASE) {
			if (key >= 0 && key < 512) {
				keys[key] = false;
			}
		} else if (action == GLFW.GLFW_PRESS) {
			if (key >= 0 && key < 512) {
				keys[key] = true;
			}
		}
	}
	
}
