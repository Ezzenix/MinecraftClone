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

const float PI = 3.1415927;

/* FOLIAGE WAVY
float t = gameTime*0.2f;
pos.x += snoise(vec2(absPos.x * 10.0, absPos.y * 10.0 + t)) * 0.05;
pos.y += snoise(vec2(absPos.y * 10.0, absPos.z * 10.0 + t)) * 0.05;
pos.z += snoise(vec2(absPos.z * 10.0, absPos.y * 10.0 + t)) * 0.05;
*/

void main() {
    vec3 pos = position;
    vec3 worldPos = (modelMatrix * vec4(position, 1.0)).xyz;

    pos.y -= 0.1;
    pos.y += (sin(worldPos.x * PI / 2 + gameTime) + sin(worldPos.z * PI / 2 + gameTime * 1.5)) * 0.03;

    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(pos, 1.0);

    texCoord = textureCoord;
    ambientOcclusion = aoFactor;

    vertexDistance = fog_distance(cameraPosition - worldPos, 0);
}