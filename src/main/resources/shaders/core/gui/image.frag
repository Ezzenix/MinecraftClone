#version 330 core

in vec2 texCoord;

out vec4 fragColor;

uniform sampler2D sampler0;

void main() {
    vec4 sampledColor = texture(sampler0, texCoord);
    fragColor = sampledColor;
}