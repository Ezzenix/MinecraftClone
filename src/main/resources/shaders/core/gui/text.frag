#version 330 core

in vec2 texCoord;
in vec4 textColor;

out vec4 fragColor;

uniform sampler2D textSampler;

void main() {
    vec4 sampledColor = texture(textSampler, texCoord);
    fragColor = sampledColor * textColor;
}