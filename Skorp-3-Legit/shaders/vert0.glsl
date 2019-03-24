#version 330 core

layout(location = 0) in vec4 in_Position;
layout(location = 1) in vec3 vertColor;

out vec3 fragColor;

uniform mat4 MVP;

void main() {
	gl_Position = MVP * in_Position;
	
	fragColor = vertColor;
}