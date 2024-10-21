#version 330

#include "noise.glsl"
#include "util.glsl"
#include "fog.glsl"

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in float aoFactor;

out vec2 texCoord;
out float ambientOcclusion;
out float vertexDistance;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform float gameTime;
uniform vec3 cameraPosition;

void main() {
    vec3 pos = position;
    vec3 absPos = pos + positionFromMatrix(modelMatrix);

    float t = gameTime*0.2f;
    pos.x += snoise(vec2(absPos.x * 10.0, absPos.y * 10.0 + t)) * 0.05;
    pos.y += snoise(vec2(absPos.y * 10.0, absPos.z * 10.0 + t)) * 0.05;
    pos.z += snoise(vec2(absPos.z * 10.0, absPos.y * 10.0 + t)) * 0.05;

    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(pos, 1.0);

    texCoord = textureCoord;
    ambientOcclusion = aoFactor;

    vec4 worldPosition = modelMatrix * vec4(position, 1.0);
    vertexDistance = fog_distance(cameraPosition - worldPosition.xyz, 0);
}