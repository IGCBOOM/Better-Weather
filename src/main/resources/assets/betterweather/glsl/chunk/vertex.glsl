#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec4 color;
layout (location = 2) in vec2 uv;
layout (location = 3) in vec2 light;
layout (location = 4) in vec3 normal;

out vec3 outPosition;
out vec4 outColor;
out vec2 outUv;
out vec2 outLight;
out vec3 outNormal;

uniform mat4 projection;
uniform mat4 modelView;

void main() {
    gl_Position = projection * modelView * vec4(position, 1.0);

    outPosition = position;
    outColor = color;
    outUv = uv;
    outLight = light;
    outNormal = normal;
}
