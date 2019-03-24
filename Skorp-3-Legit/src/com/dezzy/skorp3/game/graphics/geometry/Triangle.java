package com.dezzy.skorp3.game.graphics.geometry;

import com.dezzy.skorp3.game.math.Vec4;

public class Triangle {
	public Vec4 v0;
	public Vec4 v1;
	public Vec4 v2;
	
	public Vec4 v0c;
	public Vec4 v1c;
	public Vec4 v2c;
	
	public Triangle(final Vec4 _v0, final Vec4 _v1, final Vec4 _v2) {
		v0 = _v0;
		v1 = _v1;
		v2 = _v2;
	}
	
	public Triangle setColors(final Vec4 _v0c, final Vec4 _v1c, final Vec4 _v2c) {
		v0c = _v0c;
		v1c = _v1c;
		v2c = _v2c;
		
		return this;
	}
}
