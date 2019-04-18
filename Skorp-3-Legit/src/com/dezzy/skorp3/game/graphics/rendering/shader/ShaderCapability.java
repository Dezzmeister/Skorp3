package com.dezzy.skorp3.game.graphics.rendering.shader;

import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.LIGHT_COLOR;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.LIGHT_INTENSITY;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.LIGHT_POS;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.MODEL;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.MODELVIEW;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.MVP;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.PROJECTION;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.TEXTURE_SAMPLER;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VERTEX_COLOR;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VERTEX_NORMAL;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VERTEX_POSITION;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VERTEX_UV;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.VIEW;

public enum ShaderCapability {
	VERTICES (VERTEX_POSITION, VERTEX_UV, VERTEX_NORMAL),
	MATRICES (MODEL, VIEW, PROJECTION, MODELVIEW, MVP),
	COLORS (VERTEX_COLOR),
	TEXTURES (TEXTURE_SAMPLER),
	SHADING (LIGHT_POS, LIGHT_COLOR, LIGHT_INTENSITY);
	
	public final ShaderSubCapability[] subCapabilities;
	
	private ShaderCapability(final ShaderSubCapability ... _subCapabilities) {
		subCapabilities = _subCapabilities;
	}
}
