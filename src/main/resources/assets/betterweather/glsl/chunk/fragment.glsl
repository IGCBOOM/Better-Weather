#version 330

in vec4 outColor;
in vec2 outUv;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    fragColor = texture(textureSampler, outUv);
}
