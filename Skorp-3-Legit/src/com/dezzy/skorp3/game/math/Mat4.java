package com.dezzy.skorp3.game.math;

import java.nio.FloatBuffer;
import java.util.function.Consumer;

import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.graphics.geometry.composite.Mesh;

public class Mat4 {
	public static final Mat4 IDENTITY = new Mat4(new float[] {
		1, 0, 0, 0,
		0, 1, 0, 0,
		0, 0, 1, 0,
		0, 0, 0, 1
	});
	
	private final float[] values;
	
	/**
	 * Creates a row-major 4x4 matrix from the specified float array. Ensure that the array has 16 values.
	 * 
	 * @param _values elements of the matrix
	 */
	public Mat4(final float[] _values) {
		values = _values;
	}
	
	public Vec4 multiply(final Vec4 vec) {
		return new Vec4(
				values[0] * vec.x + values[1] * vec.y + values[2] * vec.z + values[3] * vec.w,
				values[4] * vec.x + values[5] * vec.y + values[6] * vec.z + values[7] * vec.w,
				values[8] * vec.x + values[9] * vec.y + values[10] * vec.z + values[11] * vec.w,
				values[12] * vec.x + values[13] * vec.y + values[14] * vec.z + values[15] * vec.w
		);				
	}
	
	/**
	 * Transforms a mesh, applying the transformation to the existing mesh.
	 * Multiplies this matrix by all of the mesh's triangles' vertices.
	 * 
	 * @param mesh mesh to be transformed
	 */
	public void transform(final Mesh mesh) {
		for (int i = 0; i < mesh.triangles.length; i++) {
			Triangle t = mesh.triangles[i];
			t.v0 = multiply(t.v0);
			t.v1 = multiply(t.v1);
			t.v2 = multiply(t.v2);
			
			mesh.normals[i] = multiply(mesh.normals[i]);
		}
	}
	
	public Mat4 multiply(final Mat4 mat) {
		float[] values = new float[16];
		
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				values[row * 4 + col] = rowVec(row).dot(mat.colVec(col));
			}
		}
		
		return new Mat4(values);
	}
	
	private Vec4 rowVec(int row) {
		int offset = row << 2;
		
		return new Vec4(values[offset], values[offset + 1], values[offset + 2], values[offset + 3]);
	}
	
	private Vec4 colVec(int col) {
		return new Vec4(values[col], values[col + 4], values[col + 8], values[col + 12]);
	}
	
	public void store(final FloatBuffer buf) {
		forEach(buf::put);
	}
	
	private void forEach(final Consumer<Float> floatConsumer) {
		for (int i = 0; i < values.length; i++) {
			floatConsumer.accept(values[i]);
		}
	}
	
	@Override
	public String toString() {
		String out = "";
		
		System.getProperty("line.separator");
		for (int row = 0; row < 4; row++) {
			out += "[";
			for (int col = 0; col < 4; col++) {
				out += values[row * 4 + col];
				
				if (col != 3) {
					out += ", ";
				}
			}
			out += "]";
			
			if (row != 3) {
				out += System.lineSeparator();
			}
		}
		
		return out;
	}
}
