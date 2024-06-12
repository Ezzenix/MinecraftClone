#version 330 core

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 uvCoord;
layout(location = 2) in vec3 color;

out vec2 texCoord;
out vec3 textColor;

void main() {
    gl_Position = vec4(position, 1.0, 1.0);
    texCoord = uvCoord;
    textColor = color;
}