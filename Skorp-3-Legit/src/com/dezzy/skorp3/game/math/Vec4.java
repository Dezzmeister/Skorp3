package com.dezzy.skorp3.game.math;

import java.util.Objects;

/**
 * Represents either a point or a direction in 3D space. The w component should be set to 1 to represent a point
 * and 0 to represent a direction. 
 *
 * @author Joe Desmond
 */
public class Vec4 {
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vec4 normal;
	
	public int index = -1;
	
	/**
	 * Creates a Vec4 with 4 components.
	 * 
	 * @param _x x component
	 * @param _y y component
	 * @param _z z component
	 * @param _w w component
	 */
	public Vec4(float _x, float _y, float _z, float _w) {
		x = _x;
		y = _y;
		z = _z;
		w = _w;
	}
	
	/**
	 * Creates a Vec4 with 3 components and sets w to 1.
	 * 
	 * @param _x x component
	 * @param _y y component
	 * @param _z z component
	 */
	public Vec4(float _x, float _y, float _z) {
		this(_x, _y, _z, 1);
	}
	
	/**
	 * Adds a Vec4 to this Vec4, preserving only the w component of this Vec4.
	 * <p>
	 * <b>NOTE:</b> Does not modify this Vec4. 
	 * 
	 * @param vec Vec4 to be added
	 * @return this Vec4 plus vec
	 */
	public Vec4 plus(final Vec4 vec) {
		return new Vec4(x + vec.x, y + vec.y, z + vec.z, w);
	}
	
	/**
	 * Subtracts a Vec4 from this Vec4, preserving only the w component of this Vec4.
	 * <p>
	 * <b>NOTE:</b> Does not modify this Vec4. 
	 * 
	 * @param vec Vec4 to be subtracted
	 * @return this Vec4 minus vec
	 */
	public Vec4 minus(final Vec4 vec) {
		return new Vec4(x - vec.x, y - vec.y, z - vec.z, w);
	}
	
	/**
	 * Scales the Vec4 by a given constant in each direction, preserving only the w component.
	 * <p>
	 * <b>NOTE:</b> Does not modify this Vec4. 
	 * 
	 * @param scaleX x scale factor
	 * @param scaleY y scale factor
	 * @param scaleZ z scale factor
	 * @return a scaled duplicate of this Vec4
	 */
	public Vec4 scale(float scaleX, float scaleY, float scaleZ) {
		return new Vec4(x * scaleX, y * scaleY, z * scaleZ, w);
	}
	
	/**
	 * Returns the length of this Vec4, excluding the w component.
	 * 
	 * @return length of the vector
	 */
	public float length() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}
	
	/**
	 * Normalizes this Vec4 (i.e., returns an equivalent Vec4 with a magnitude of 1), excluding the w component, which is set to 1.
	 * <p>
	 * <b>NOTE:</b> Does not modify this Vec4. 
	 * 
	 * @return a normalized version of this Vec4
	 */
	public Vec4 normalize() {
		float invLen = 1.0f / length();
		return scale(invLen, invLen, invLen);
	}
	
	/**
	 * Calculates the dot product of two Vec4s, including their w components.
	 * 
	 * @param vec other Vec4
	 * @return dot product
	 */
	public float dot(final Vec4 vec) {
		return x * vec.x + y * vec.y + z * vec.z + w * vec.w;
	}
	
	/**
	 * Calculates the 3-component cross product of two vectors, excluding the w components. Returns a new vector
	 * with a w component of 1.
	 * <p>
	 * <b>NOTE:</b> Does not modify this Vec4.
	 * 
	 * @param a first Vec4
	 * @param b second Vec4
	 * @return cross product of a and b
	 */
	public static Vec4 cross(final Vec4 a, final Vec4 b) {
		return new Vec4(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x, 1);
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + "," + w + ")";
	}
	
	private static final int PLACES = 4;
	
	@Override
	public boolean equals(final Object v) {
		if (this == v) {
			return true;
		}
		
		if (!(v instanceof Vec4)) {
			return false;
		}
		
		Vec4 vec = (Vec4) v;
		return (round(x, PLACES) == round(vec.x, PLACES)) && 
			   (round(y, PLACES) == round(vec.y, PLACES)) && 
			   (round(z, PLACES) == round(vec.z, PLACES));
	}
	
	@Override
	public int hashCode() {
		
		float x0 = round(x, PLACES);
		
		float y0 = round(x, PLACES);
		
		float z0 = round(x, PLACES);
		
		return Objects.hash(x0, y0, z0);
	}
	
	private static float round(float value, int places) {
		double scale = Math.pow(10, places);
		return (float) (Math.round(value * scale) / scale);
	}
}
