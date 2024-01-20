#version 330

in vec2 texCoord;
in float ambientOcclusion;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec2 textureAtlasSize;

void main() {
    // get color on texture
    vec2 numTiles = floor(texCoord);
    if (numTiles.x < 1 || numTiles.y < 1) { // tile size is below 1 which is not possible.
        numTiles = vec2(1, 1);
    }
    vec2 atlasUV = fract(texCoord);
    vec2 atlasTileSizeNormalized = 16.0 / textureAtlasSize; // how big one tile of atlas is
    vec2 topLeftUV = floor(atlasUV / atlasTileSizeNormalized) * atlasTileSizeNormalized; // the UV coordinates at the top-left of the texture in the atlas
    vec2 uv = texCoord - numTiles - topLeftUV; // uv coordinates relative to the top-left corner
    vec2 uvAlpha = uv / atlasTileSizeNormalized;
    vec2 repeatedUVAlpha = mod(uvAlpha * numTiles, 1);
    vec2 final = topLeftUV + repeatedUVAlpha * atlasTileSizeNormalized;
    vec4 textureColor = texture(textureSampler, final);

    // apply ambient occlusion
    textureColor.rgb *= (1.0 - ambientOcclusion*0.8f);

    fragColor = textureColor;
}