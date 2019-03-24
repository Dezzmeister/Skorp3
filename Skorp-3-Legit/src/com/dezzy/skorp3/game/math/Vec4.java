package com.dezzy.skorp3.game.math;


public class Vec4 {
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vec4(float _x, float _y, float _z, float _w) {
		x = _x;
		y = _y;
		z = _z;
		w = _w;
	}
	
	public Vec4(float _x, float _y, float _z) {
		this(_x, _y, _z, 1);
	}
	
	public Vec4 plus(final Vec4 vec) {
		return new Vec4(x + vec.x, y + vec.y, z + vec.z, w);
	}
	
	public Vec4 minus(final Vec4 vec) {
		return new Vec4(x - vec.x, y - vec.y, z - vec.z, w);
	}
	
	public Vec4 scale(float scaleX, float scaleY, float scaleZ) {
		return new Vec4(x * scaleX, y * scaleY, z * scaleZ, w);
	}
	
	public float length() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}
	
	public Vec4 normalize() {
		float invLen = 1.0f / length();
		return scale(invLen, invLen, invLen);
	}
	
	public float dot(final Vec4 vec) {
		return x * vec.x + y * vec.y + z * vec.z + w * vec.w;
	}
	
	public static Vec4 cross(final Vec4 a, final Vec4 b) {
		return new Vec4(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x, 1);
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + "," + w + ")";
	}
}
