package com.dezzy.skorp3.game.graphics.rendering.shader;


public enum ShaderSubCapability {
	//Vertex capabilities
	VERTEX_POSITION,
	VERTEX_UV,
	VERTEX_NORMAL,
	
	//Matrix capabilites
	MODEL_MATRIX,
	VIEW_MATRIX,
	PROJECTION_MATRIX,
	MODELVIEW_MATRIX,
	MVP_MATRIX,
	
	//Lighting capabilities
	LIGHT_POS,
	LIGHT_COLOR,
	LIGHT_INTENSITY,
	
	//Texturing capabilities
	TEXTURE_SAMPLER,
	
	//Color capabilities
	VERTEX_COLOR
}
