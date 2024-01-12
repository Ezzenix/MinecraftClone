#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;

out vec2 fragTextureCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 chunkPosition;

void main() {
    gl_Position = projectionMatrix * viewMatrix * chunkPosition * vec4(position, 1.0);
    fragTextureCoord = textureCoord;
}