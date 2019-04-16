package com.dezzy.skorp3.game.graphics.geometry.complex;

import com.dezzy.skorp3.game.graphics.geometry.composite.Mesh;
import com.dezzy.skorp3.game.math.Mat4;

public class MovableMesh {
	public Mesh mesh;
	public Mat4 modelMatrix;
	public Mat4 mvpMatrix;
	
	public MovableMesh(final Mesh _mesh, final Mat4 _modelMatrix) {
		mesh = _mesh;
		modelMatrix = _modelMatrix;
	}
	
	public MovableMesh setModelMatrix(final Mat4 _modelMatrix) {
		modelMatrix = _modelMatrix;
		
		return this;
	}
	
	public MovableMesh setMVPMatrix(final Mat4 _mvpMatrix) {
		mvpMatrix = _mvpMatrix;
		
		return this;
	}
}
