#version 330 core

#include "color.glsl"

layout(location = 0) in vec3 position;
layout(location = 1) in int lineColor;

out vec4 color;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);
    color = unpackColor(lineColor);
}