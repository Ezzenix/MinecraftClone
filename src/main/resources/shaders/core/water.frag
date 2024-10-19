#version 330

in vec2 texCoord;
in float ambientOcclusion;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    vec4 textureColor = texture(textureSampler, texCoord);

    // apply ambient occlusion
    textureColor.rgb *= (1.0 - ambientOcclusion*0.75f);

    fragColor = textureColor;
}