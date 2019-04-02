package com.dezzy.skorp3.game.graphics.geometry.composite;

import com.dezzy.skorp3.game.graphics.Texture;
import com.dezzy.skorp3.game.graphics.utils.TransformUtils;

public class Block extends Mesh {
	private static final float HALF_PI = (float)Math.PI / 2.0f;
	public Square[] faces;
	
	public Block(final Texture texture) {
		faces = new Square[6];
		
		faces[0] = new Square().setTexture(texture);
		TransformUtils.translate(0, 0, 0.5f).transform(faces[0]);
		
		faces[1] = new Square().setTexture(texture);
		TransformUtils.translate(0, 0, -0.5f).transform(faces[1]);
		
		faces[2] = new Square().setTexture(texture);
		TransformUtils.translate(0.5f, 0, 0).multiply(TransformUtils.rotateY(HALF_PI)).transform(faces[2]);
		
		faces[3] = new Square().setTexture(texture);
		TransformUtils.translate(-0.5f, 0, 0).multiply(TransformUtils.rotateY(HALF_PI)).transform(faces[3]);
		
		faces[4] = new Square().setTexture(texture);
		TransformUtils.translate(0, 0.5f, 0).multiply(TransformUtils.rotateX(HALF_PI)).transform(faces[4]);
		
		faces[5] = new Square().setTexture(texture);
		TransformUtils.translate(0, -0.5f, 0).multiply(TransformUtils.rotateX(HALF_PI)).transform(faces[5]);
		
		this.triangles = faces[0].add(faces[1]).add(faces[2]).add(faces[3]).add(faces[4]).add(faces[5]).triangles;
	}
}
