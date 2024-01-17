#version 330

layout(location = 0) in vec3 position;

//out vec3 fragColor;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
//uniform vec3 lineColor;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);
    //fragColor = lineColor;
}