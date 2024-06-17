#version 330 core

in vec2 texCoord;
in vec3 textColor;

out vec4 fragColor;

uniform sampler2D textSampler;

void main() {
    vec4 sampledColor = texture(textSampler, texCoord);
    fragColor = sampledColor * vec4(textColor, 1);
}