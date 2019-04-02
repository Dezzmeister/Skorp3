#version 330 core

layout(location = 0) in vec3 vertPosMS;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec3 vertexNormal;

out vec2 UV;
out vec3 posWS;
out vec3 normalCS;
out vec3 eyeDirCS;
out vec3 lightDirCS;

uniform mat4 MVP;
uniform mat4 M;
uniform mat4 V;
uniform vec3 lightPosWS;

void main() {
	gl_Position = MVP * vec4(vertPosMS, 1);
	
	posWS = (M * vec4(vertPosMS, 1)).xyz;
	
	vec3 vertPosCS = (V * M * vec4(vertPosMS, 1)).xyz;
	eyeDirCS = vec3(0, 0, 0) - vertPosCS;
	
	vec3 lightPosCS = (V * vec4(lightPosWS, 1)).xyz;
	lightDirCS = lightPosCS + eyeDirCS;
	
	normalCS = (V * M * vec4(vertexNormal, 0)).xyz;
	
	UV = vertexUV;
}