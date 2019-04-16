package com.dezzy.skorp3.game.graphics.rendering;

import org.lwjgl.opengl.GL11;

import com.dezzy.skorp3.game.graphics.geometry.composite.Mesh;

public class GL11Renderer extends GLRenderer {
	private volatile Mesh mesh = null;
	
	public GL11Renderer() {
		
	}
	
	@Override
	public void render() {
		GL11.glBegin(GL11.GL_TRIANGLES);
	}
	
	public void setMesh(final Mesh _mesh) {
		mesh = _mesh;
	}
	
}
