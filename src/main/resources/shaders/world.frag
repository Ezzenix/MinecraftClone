#version 330

in vec2 fragTextureCoord;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    vec2 numTiles = floor(fragTextureCoord);
    vec2 tilingTexCoords = fragTextureCoord;

    if (numTiles.xy != vec2(0, 0)) {
        // Get the original texture coordinates
        tilingTexCoords = (fragTextureCoord - numTiles);

        // Get the top-left corner of the tile's texture coordinates
        vec2 flooredTexCoords = floor((fragTextureCoord - numTiles) * 16.0) / 16.0;
        numTiles = numTiles + vec2(1, 1);

        // Use modulo to repeat the tile
        tilingTexCoords = flooredTexCoords + mod(((tilingTexCoords - flooredTexCoords) * numTiles) * 16.0, 1.0) / 16.0;
    }

    fragColor = texture(textureSampler, tilingTexCoords);
}