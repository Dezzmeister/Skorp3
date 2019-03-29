package com.dezzy.skorp3.game.graphics.geometry.composite;

import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.math.Vec4;
import com.dezzy.skorp3.logging.Logger;

public class Mesh {
	public Triangle[] triangles;
	public Vec4[] normals;
	
	/**
	 * Constructs a Mesh from triangles and vertex normals. The vertex normal array needs to be three times
	 * the size of the triangle array.
	 * 
	 * @param _triangles triangles
	 * @param _normals vertex normals
	 */
	public Mesh(final Triangle[] _triangles, final Vec4[] _normals) {
		triangles = _triangles;
		normals = _normals;
		
		if (normals.length != triangles.length * 3) {
			triangles = null;
			normals = null;
			
			Logger.error("Vertex normal array must be three times the size of the triangle array!");
		}
	}
	
	public Mesh(final Triangle ... _triangles) {
		this(_triangles, null);
	}
	
	public Mesh add(final Mesh mesh) {
		Triangle[] newTriangles = new Triangle[triangles.length + mesh.triangles.length];
		Vec4[] newNormals = new Vec4[(triangles.length + mesh.triangles.length) * 3];
		
		for (int i = 0; i < triangles.length; i++) {
			newTriangles[i] = triangles[i];
			newNormals[i] = normals[i];
		}
		
		for (int i = normals.length; i < newNormals.length; i++) {
			newTriangles[i] = mesh.triangles[i - triangles.length];
			newNormals[i] = mesh.normals[i - normals.length];
		}
		
		return new Mesh(newTriangles);
	}
	
	public float[] getVBOVertices() {
		float[] vertices = new float[triangles.length * 9];
		
		for (int i = 0; i < triangles.length; i++) {
			Triangle t = triangles[i];
			int offset = i * 9;
			
			vertices[offset] = t.v0.x;
			vertices[offset + 1] = t.v0.y;
			vertices[offset + 2] = t.v0.z;
			
			vertices[offset + 3] = t.v1.x;
			vertices[offset + 4] = t.v1.y;
			vertices[offset + 5] = t.v1.z;
			
			vertices[offset + 6] = t.v2.x;
			vertices[offset + 7] = t.v2.y;
			vertices[offset + 8] = t.v2.z;
		}
		
		return vertices;
	}
	
	public float[] getColors() {
		float[] colors = new float[triangles.length * 9];
		
		for (int i = 0; i < triangles.length; i++) {
			Triangle t = triangles[i];
			int offset = i * 9;
			
			colors[offset] = t.v0c.x;
			colors[offset + 1] = t.v0c.y;
			colors[offset + 2] = t.v0c.z;
			
			colors[offset + 3] = t.v1c.x;
			colors[offset + 4] = t.v1c.y;
			colors[offset + 5] = t.v1c.z;
			
			colors[offset + 6] = t.v2c.x;
			colors[offset + 7] = t.v2c.y;
			colors[offset + 8] = t.v2c.z;
		}
		
		return colors;
	}
}
