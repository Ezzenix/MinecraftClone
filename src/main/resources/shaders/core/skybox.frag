#version 330 core

#include "color.glsl"

in vec2 uv;

out vec4 fragColor;

uniform sampler2D sampler0;

void main() {
    vec4 color = texture(sampler0, uv);
    fragColor = color;
}