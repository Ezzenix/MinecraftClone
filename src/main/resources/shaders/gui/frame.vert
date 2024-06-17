#version 330 core

#include "color.glsl"

layout(location = 0) in vec2 position;
layout(location = 1) in vec4 color;

out vec4 outColor;

void main() {
    gl_Position = vec4(position, 1.0, 1.0);
    outColor = color;
}