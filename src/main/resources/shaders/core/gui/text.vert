#version 330 core

#include "color.glsl"

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 uvCoord;
layout(location = 2) in int color;

out vec2 texCoord;
out vec4 textColor;

void main() {
    gl_Position = vec4(position, 1.0, 1.0);
    texCoord = uvCoord;
    textColor = unpackColor(color);
}