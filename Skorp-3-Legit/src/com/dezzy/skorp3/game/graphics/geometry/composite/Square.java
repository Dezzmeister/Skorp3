package com.dezzy.skorp3.game.graphics.geometry.composite;

import com.dezzy.skorp3.game.graphics.Texture;
import com.dezzy.skorp3.game.graphics.geometry.Mesh;
import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.math.Vec2;
import com.dezzy.skorp3.game.math.Vec4;

public class Square extends Mesh {
	
	/**
	 * Creates a 2 x 2 square on the XY plane.
	 */
	public Square() {
		triangles = new Triangle[2];
		
		Vec4 v0 = new Vec4(-1, 1, 0);
		Vec4 v1 = new Vec4(-1, -1, 0);
		Vec4 v2 = new Vec4(1, -1, 0);
		Vec4 v3 = new Vec4(1, 1, 0);
		
		triangles[0] = new Triangle(v0, v1, v2);
		triangles[1] = new Triangle(v2, v3, v0);
	}
	
	public Square setTexture(final Texture texture) {
		Vec2 uv0 = new Vec2(0, 0);
		Vec2 uv1 = new Vec2(0, 1);
		Vec2 uv2 = new Vec2(1, 1);
		Vec2 uv3 = new Vec2(1, 0);
		
		triangles[0].setUV(uv0, uv1, uv2);
		triangles[0].setTexture(texture);
		
		triangles[1].setUV(uv2, uv3, uv0);
		triangles[1].setTexture(texture);
		
		return this;
	}
}
