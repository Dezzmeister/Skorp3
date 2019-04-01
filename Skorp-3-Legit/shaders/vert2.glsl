#version 330 core

layout(location = 0) in vec4 in_Position;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec3 normalbuffer;

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
	gl_Position = MVP * in_Position;
	
	UV = vertexUV;
}