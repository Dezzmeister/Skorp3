package com.dezzy.skorp3.game.graphics.geometry.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.math.Vec4;

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
		
		if (normals != null && (normals.length != triangles.length * 3)) {
			triangles = null;
			normals = null;
		}
	}
	
	public Mesh(final Triangle ... _triangles) {
		this(_triangles, null);
	}
	
	public Mesh add(final Mesh mesh) {
		Triangle[] newTriangles = new Triangle[triangles.length + mesh.triangles.length];
		
		for (int i = 0; i < triangles.length; i++) {
			newTriangles[i] = triangles[i];
		}
		
		for (int i = triangles.length; i < newTriangles.length; i++) {
			newTriangles[i] = mesh.triangles[i - triangles.length];
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
	
	public void resolveNormals() {
		Map<Vec4, List<Triangle>> connectedTriangles = new HashMap<Vec4, List<Triangle>>();
		
		for (int i = 0; i < triangles.length; i++) {
			Triangle t = triangles[i];
			
			Vec4 v0 = t.v0;
			Vec4 v1 = t.v1;
			Vec4 v2 = t.v2;
			
			addTriangleForVertex(connectedTriangles, v0, t);
			addTriangleForVertex(connectedTriangles, v1, t);
			addTriangleForVertex(connectedTriangles, v2, t);
		}
		
		for (Entry<Vec4, List<Triangle>> entry : connectedTriangles.entrySet()) {
			Vec4 normal = new Vec4(0, 0, 0);
			Vec4 vertex = entry.getKey();
			List<Triangle> triangles = entry.getValue();
			
			for (Triangle t : triangles) {
				normal = normal.plus(t.normal);
			}
			
			normal = normal.normalize();
			
			for (int i = 0; i < triangles.size(); i++) {
				Triangle t = triangles.get(i);
				
				if (t.v0.equals(vertex)) {
					t.v0.normal = normal;
				}
				
				if (t.v1.equals(vertex)) {
					t.v1.normal = normal;
				}
				
				if (t.v2.equals(vertex)) {
					t.v2.normal = normal;
				}
			}
		}
		
		normals = new Vec4[triangles.length * 3];
		for (int i = 0; i < triangles.length; i++) {
			Triangle t = triangles[i];			
			int offset = i * 3;
			
			normals[offset] = t.v0.normal;
			normals[offset + 1] = t.v1.normal;
			normals[offset + 2] = t.v2.normal;
		}
	}
	
	private void addTriangleForVertex(final Map<Vec4, List<Triangle>> connectedTriangles, final Vec4 vec, final Triangle triangle) {
		if (connectedTriangles.get(vec) == null) {
			
			List<Triangle> triangles = new ArrayList<Triangle>();
			triangles.add(triangle);
			
			connectedTriangles.put(vec, triangles);
		} else {
			connectedTriangles.get(vec).add(triangle);
		}
	}
}
