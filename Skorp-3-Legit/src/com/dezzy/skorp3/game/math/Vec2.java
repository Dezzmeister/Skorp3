package com.dezzy.skorp3.game.math;


public class Vec2 {
	public float x;
	public float y;
	
	public Vec2(float _x, float _y) {
		x = _x;
		y = _y;
	}
	
	public Vec2 plus(final Vec2 vec) {
		return new Vec2(x + vec.x, y + vec.y);
	}
	
	public Vec2 minus(final Vec2 vec) {
		return new Vec2(x - vec.x, y - vec.y);
	}
	
	public float dot(final Vec2 vec) {
		return x * vec.x + y * vec.y;
	}
}
