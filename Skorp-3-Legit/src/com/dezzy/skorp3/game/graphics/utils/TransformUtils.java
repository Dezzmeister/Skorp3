package com.dezzy.skorp3.game.graphics.utils;

import com.dezzy.skorp3.game.math.Mat4;
import com.dezzy.skorp3.game.math.Vec4;

public class TransformUtils {
	
	public static Mat4 perspective(float fovy, float aspect, float zNear, float zFar) {
		float tanHalfFovy = (float) Math.tan(fovy / 2.0f);
		
		float[] values = new float[] {
				1.0f / (aspect * tanHalfFovy), 0, 0, 0,
				0, 1.0f / tanHalfFovy, 0, 0,
				0, 0, -(zFar + zNear) / (zFar - zNear), -(2 * zFar * zNear) / (zFar - zNear),
				0, 0, -1, 0
		};
		
		return new Mat4(values);
	}
	
	public static Mat4 lookAt(final Vec4 eye, final Vec4 center, final Vec4 up) {
		Vec4 Z = eye.minus(center).normalize();
		Vec4 X = Vec4.cross(up, Z).normalize();
		Vec4 Y = Vec4.cross(Z, X).normalize();
		
		return new Mat4(new float[] {
			X.x, X.y, X.z, -X.dot(eye),
			Y.x, Y.y, Y.z, -Y.dot(eye),
			Z.x, Z.y, Z.z, -Z.dot(eye),
			0, 0, 0, 1.0f
		});
	}
}
