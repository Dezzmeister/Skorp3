package com.dezzy.skorp3.game.graphics.geometry;

import com.dezzy.skorp3.game.graphics.Texture;
import com.dezzy.skorp3.game.math.Vec2;
import com.dezzy.skorp3.game.math.Vec4;

public class Triangle {
	public Vec4 v0;
	public Vec4 v1;
	public Vec4 v2;
	
	public Vec4 normal;
	
	public Vec4 v0c;
	public Vec4 v1c;
	public Vec4 v2c;
	
	public Vec2 uv0;
	public Vec2 uv1;
	public Vec2 uv2;
	
	public Texture tex;
	
	public Triangle(final Vec4 _v0, final Vec4 _v1, final Vec4 _v2) {
		v0 = _v0;
		v1 = _v1;
		v2 = _v2;
		
		normal  = Vec4.cross(v1.minus(v0), v2.minus(v0)).normalize();
	}
	
	public Triangle setUV(final Vec2 _uv0, final Vec2 _uv1, final Vec2 _uv2) {
		uv0 = _uv0;
		uv1 = _uv1;
		uv2 = _uv2;
		
		return this;
	}
	
	public Triangle setTexture(final Texture _tex) {
		tex = _tex;
		
		return this;
	}
	
	public Triangle setColors(final Vec4 _v0c, final Vec4 _v1c, final Vec4 _v2c) {
		v0c = _v0c;
		v1c = _v1c;
		v2c = _v2c;
		
		return this;
	}
}
