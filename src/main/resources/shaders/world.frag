#version 330

in vec2 texCoord;
in float ambientOcclusion;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec2 textureAtlasSize;

void main() {
    vec2 numTiles = floor(texCoord);
    vec2 atlasUV = texCoord - floor(texCoord);
    vec2 atlasTileSizeNormalized = vec2(16.0 / textureAtlasSize.x, 16.0 / textureAtlasSize.y); // how big one tile of atlas is
    vec2 topLeftUV = floor(atlasUV / atlasTileSizeNormalized) * atlasTileSizeNormalized; // the UV coordinates at the top-left of the texture in the atlas
    vec2 uv = texCoord - numTiles - topLeftUV; // uv coordinates relative to the top-left corner
    vec2 uvAlpha = vec2(uv.x / atlasTileSizeNormalized.x, uv.y / atlasTileSizeNormalized.y);
    vec2 repeatedUVAlpha = vec2(mod(uvAlpha.x * numTiles.x, 1), mod(uvAlpha.y * numTiles.y, 1));

    vec2 final = topLeftUV + vec2(repeatedUVAlpha.x * atlasTileSizeNormalized.x, repeatedUVAlpha.y * atlasTileSizeNormalized.y);

    vec4 textureColor = texture(textureSampler, final);

    textureColor.rgb *= (1.0 - ambientOcclusion*0.8f);

    fragColor = textureColor;
}