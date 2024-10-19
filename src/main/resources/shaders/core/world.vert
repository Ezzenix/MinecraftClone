#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in float aoFactor;

out vec2 texCoord;
out float ambientOcclusion;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    texCoord = textureCoord;
    ambientOcclusion = aoFactor;
}