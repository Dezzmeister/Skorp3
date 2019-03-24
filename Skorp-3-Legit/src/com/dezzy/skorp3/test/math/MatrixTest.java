package com.dezzy.skorp3.test.math;

import com.dezzy.skorp3.game.graphics.utils.TransformUtils;
import com.dezzy.skorp3.game.math.Mat4;
import com.dezzy.skorp3.game.math.Vec4;

public class MatrixTest {
	
	public static void testOperations() {
		Mat4 mat0 = new Mat4(new float[] {
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16
		});
		
		Vec4 vec0 = new Vec4(3, 5.5f, 2, 1.4f);
		
		//System.out.println(mat0);
		//System.out.println(mat0.multiply(vec0));
		
		Mat4 mat1 = new Mat4(new float[] {
				9, 3, 4, 5,
				1, 0.4f, 3, 1,
				8, 4, 3, 5,
				1, 3, 4, 2
		});
		
		System.out.println(mat0.multiply(mat1));
	}
	
	public static void testTransformations() {
		Mat4 translate = new Mat4(new float[] {
				1, 0, 0, 10,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
		});
		
		Mat4 scale = new Mat4(new float[] {
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 1
		});
		
		Vec4 vec0 = new Vec4(5, 3, 8, 1);
		System.out.println(translate.multiply(scale).multiply((vec0)));
	}
	
	public static Mat4 getTestMVP() {
		Mat4 projection = TransformUtils.perspective((float)Math.PI / 4.0f, 1, 0.1f, 100.0f);
		
		Mat4 view = TransformUtils.lookAt(
				new Vec4(4, 3, 3, 1),
				new Vec4(0, 0, 0, 1),
				new Vec4(0, 1, 0, 1)
		);
		
		Mat4 model = Mat4.IDENTITY;
		
		//Mat4 mvp = projection.multiply(view).multiply(model);
		Mat4 mvp = model.multiply(view).multiply(projection);
		
		return mvp;
	}
}
