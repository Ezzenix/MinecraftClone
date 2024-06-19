#version 330 core

#include "color.glsl"

in vec2 uv;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    fragColor = texture(textureSampler, uv);
}