#version 330

#include "noise.glsl"
#include "util.glsl"

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in float aoFactor;

out vec2 texCoord;
out float ambientOcclusion;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 chunkPosition;

uniform float time;

void main() {
    vec3 pos = position;
    vec3 absPos = pos + positionFromMatrix(chunkPosition);

    float t = time*0.2f;
    pos.x += snoise(vec2(absPos.x * 10.0, absPos.y * 10.0 + t)) * 0.05;
    pos.y += snoise(vec2(absPos.y * 10.0, absPos.z * 10.0 + t)) * 0.05;
    pos.z += snoise(vec2(absPos.z * 10.0, absPos.y * 10.0 + t)) * 0.05;

    gl_Position = projectionMatrix * viewMatrix * chunkPosition * vec4(pos, 1.0);

    texCoord = textureCoord;
    ambientOcclusion = aoFactor;
}