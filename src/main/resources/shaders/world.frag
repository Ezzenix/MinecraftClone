#version 330

in vec2 fragTextureCoord;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    fragColor = texture(textureSampler, fragTextureCoord - floor(fragTextureCoord));
}