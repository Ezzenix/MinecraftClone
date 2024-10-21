#version 330

#include "fog.glsl"
#include "color.glsl"

in vec2 texCoord;
in float ambientOcclusion;
in float vertexDistance;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform float fogStart;
uniform float fogEnd;
uniform int fogColor;

void main() {
    vec4 color = texture(textureSampler, texCoord);

    // apply ambient occlusion
    color.rgb *= (1.0 - ambientOcclusion*0.8f);

    fragColor = linear_fog(color, vertexDistance, fogStart, fogEnd, unpackColor(fogColor));;
}