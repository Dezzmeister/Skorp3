#version 330 core

in vec2 UV;
in vec3 posWS;
in vec3 normalCS;
in vec3 eyeDirCS;
in vec3 lightDirCS;

out vec3 color;

uniform sampler2D textureSampler;
uniform mat4 MV;
uniform vec3 lightPosWS;

void main() {
	vec3 lightColor = vec3(0.5f, 1, 0.0f);
	float lightPower = 50.0f;
	
	vec3 matDiffuseColor = texture(textureSampler, UV).rgb;
	vec3 matAmbientColor = vec3(0.1, 0.1, 0.1) * matDiffuseColor;
	vec3 matSpecularColor = vec3(0.3, 0.3, 0.3);
	
	float distance = length(lightPosWS - posWS);
	
	vec3 n = normalize(normalCS);
	vec3 l = normalize(lightDirCS);
	float cosTheta = clamp(dot(n, l), 0, 1);
	
	vec3 E = normalize(eyeDirCS);
	vec3 R = reflect(-l, n);
	float cosAlpha = clamp(dot(E, R), 0, 1);
	
	color = matAmbientColor + 
			matDiffuseColor * lightColor * lightPower * cosTheta / (distance * distance) + 
			matSpecularColor * lightColor * lightPower * pow(cosAlpha, 5) / (distance * distance);
}