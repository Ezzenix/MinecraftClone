/*
#version 330 core

in vec2 texCoord;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec4 color;

void main() {
    vec4 texColor = texture(textureSampler, texCoord);
    fragColor = texColor * color;
}
*/

#version 330 core

//in vec2 texCoord;

out vec4 fragColor;

//uniform sampler2D textureSampler;
//uniform vec4 color;

void main() {
    fragColor = vec4(1, 1, 1, 1);
}