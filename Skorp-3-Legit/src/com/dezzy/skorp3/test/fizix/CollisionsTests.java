package com.dezzy.skorp3.test.fizix;

import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.math.Vec4;

public class CollisionsTests {
	
	public static void main(String[] args) {
		planeSideTest();
	}
	
	public static void planeSideTest() {
		Triangle triangle = new Triangle(new Vec4(0, 0, -1), new Vec4(1, 0, -1), new Vec4(1, 1, -1));
		Vec4 delta = new Vec4(0, 0, 1);
		Vec4 delta2 = new Vec4(0, 0, -2);
		
		System.out.println(Vec4.cross(delta, triangle.normal));
		System.out.println(Vec4.cross(delta2, triangle.normal));
	}
}
