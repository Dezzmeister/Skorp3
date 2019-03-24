package com.dezzy.skorp3.game.graphics.utils;

import com.dezzy.skorp3.game.math.Mat4;
import com.dezzy.skorp3.game.math.Vec4;

public class TransformUtils {
	
	public static Mat4 perspective(float fovy, float aspect, float zNear, float zFar) {
		float yScale = (float) (1.0f / Math.tan(fovy / 2.0f));
		float xScale = yScale / aspect;
		float frustumLength = zFar - zNear;
		
		return new Mat4(new float[] {
				xScale, 0, 0, 0,
				0, yScale, 0, 0,
				0, 0, -((zFar + zNear) / frustumLength), -((2 * zFar * zNear) / frustumLength),
				0, 0, -1, 0
		});
	}
	
	public static Mat4 lookAt(final Vec4 eye, final Vec4 center, final Vec4 up) {
		Vec4 Z = center.minus(eye).normalize();
		Vec4 X = Vec4.cross(Z, up).normalize();
		Vec4 Y = Vec4.cross(X, Z).normalize();
		
		return new Mat4(new float[] {
				X.x, X.y, X.z, -X.dot(eye),
				Y.x, Y.y, Y.z, -Y.dot(eye),
				-Z.x, -Z.y, -Z.z, Z.dot(eye),
				0, 0, 0, 1.0f
		});
	}
	
	public static Mat4 translate(float x, float y, float z) {
		return new Mat4(new float[] {
				1, 0, 0, x,
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1
		});
	}
	
	public static Mat4 scale(float x, float y, float z) {
		return new Mat4(new float[] {
				x, 0, 0, 0,
				0, y, 0, 0,
				0, 0, z, 0,
				0, 0, 0, 1
		});
	}
	
	public static Mat4 rotateX(float f) {
		return new Mat4(new float[] {
				1, 0, 0, 0,
				0, cosf(f), -sinf(f), 0,
				0, sinf(f), cosf(f), 0,
				0, 0, 0, 1
		});
	}
	
	public static Mat4 rotateY(float f) {
		return new Mat4(new float[] {
				cosf(f), 0, sinf(f), 0,
				0, 1, 0, 0,
				-sinf(f), 0, cosf(f), 0,
				0, 0, 0, 1
		});
	}
	
	public static Mat4 rotateZ(float f) {
		return new Mat4(new float[] {
				cosf(f), -sinf(f), 0, 0,
				sinf(f), cosf(f), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
		});
	}
	
	private static float cosf(float f) {
		return (float) Math.cos(f);
	}
	
	private static float sinf(float f) {
		return (float) Math.sin(f);
	}
}
