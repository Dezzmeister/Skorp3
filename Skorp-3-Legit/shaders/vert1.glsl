#version 330 core

layout(location = 0) in vec4 in_Position;
layout(location = 1) in vec2 vertexUV;

out vec2 UV;

uniform mat4 MVP;

void main() {
	gl_Position = MVP * in_Position;
	
	UV = vertexUV;
}