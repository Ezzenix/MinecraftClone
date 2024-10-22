#version 330

in vec2 texCoord;

out vec4 fragColor;

uniform sampler2D sampler0;

void main() {
    vec4 textureColor = texture(sampler0, texCoord);
    fragColor = textureColor;
}