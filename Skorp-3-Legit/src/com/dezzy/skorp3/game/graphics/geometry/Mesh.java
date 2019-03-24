package com.dezzy.skorp3.game.graphics.geometry;


public class Mesh {
	public Triangle[] triangles;
	
	public Mesh(final Triangle ... _triangles) {
		triangles = _triangles;
	}
	
	public Mesh add(final Mesh mesh) {
		Triangle[] newTriangles = new Triangle[triangles.length + mesh.triangles.length];
		
		for (int i = 0; i < triangles.length; i++) {
			newTriangles[i] = triangles[i];
		}
		
		for (int i = triangles.length; i < newTriangles.length; i++) {
			newTriangles[i] = mesh.triangles[i - triangles.length];
		}
		
		return new Mesh(triangles);
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
