package com.dezzy.skorp3.game.graphics.rendering.shader;

import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.LIGHT_COLOR;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.LIGHT_INTENSITY;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.LIGHT_POS;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.MODEL_MATRIX;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.MODELVIEW_MATRIX;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.MVP_MATRIX;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.PROJECTION_MATRIX;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.TEXTURE_SAMPLER;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VERTEX_COLOR;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VERTEX_NORMAL;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VERTEX_POSITION;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VERTEX_UV;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VIEW_MATRIX;

public enum ShaderCapability {
	VERTICES (VERTEX_POSITION, VERTEX_UV, VERTEX_NORMAL),
	MATRICES (MODEL_MATRIX, VIEW_MATRIX, PROJECTION_MATRIX, MODELVIEW_MATRIX, MVP_MATRIX),
	COLORS (VERTEX_COLOR),
	TEXTURES (TEXTURE_SAMPLER),
	LIGHTING (LIGHT_POS, LIGHT_COLOR, LIGHT_INTENSITY);
	
	public final ShaderSubCapability[] subCapabilities;
	
	private ShaderCapability(final ShaderSubCapability ... _subCapabilities) {
		subCapabilities = _subCapabilities;
	}
}
