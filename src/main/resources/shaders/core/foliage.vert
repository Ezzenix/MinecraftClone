#version 330

#include "noise.glsl"
#include "util.glsl"
#include "fog.glsl"

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in float aoFactor;
layout(location = 3) in int shouldWave;

out vec2 texCoord;
out float ambientOcclusion;
out float vertexDistance;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform float gameTime;
uniform vec3 cameraPosition;

const float PI = 3.1415927;

void main() {
    vec3 pos = position;
    vec3 worldPos = (modelMatrix * vec4(position, 1.0)).xyz;

    if (shouldWave == 1) {
        float t = gameTime*0.2f;
        pos.x += snoise(vec2(worldPos.x * 10.0, worldPos.y * 10.0 + t)) * 0.05;
        pos.y += snoise(vec2(worldPos.y * 10.0, worldPos.z * 10.0 + t)) * 0.05;
        pos.z += snoise(vec2(worldPos.z * 10.0, worldPos.y * 10.0 + t)) * 0.05;
    }

    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(pos, 1.0);

    texCoord = textureCoord;
    ambientOcclusion = aoFactor;

    vertexDistance = fog_distance(cameraPosition - worldPos, 0);
}