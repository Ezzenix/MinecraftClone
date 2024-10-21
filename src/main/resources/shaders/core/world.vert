#version 330

#include "fog.glsl"
#include "util.glsl"

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in float aoFactor;

out vec2 texCoord;
out float ambientOcclusion;
out float vertexDistance;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    texCoord = textureCoord;
    ambientOcclusion = aoFactor;

    vec4 worldPosition = modelMatrix * vec4(position, 1.0);
    vertexDistance = fog_distance(cameraPosition - worldPosition.xyz, 0);
}