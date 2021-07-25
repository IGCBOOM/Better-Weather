#version 330

in vec3 outPosition;
in vec4 outColor;
in vec2 outUv;
in vec2 outLight;
in vec3 outNormal;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform sampler2D lightmapSampler;
uniform vec4 rgba;

void main() {
    //vec4(outNormal * 0.5 + 0.5, 1.0)

    vec4 texture = texture(textureSampler, outUv) * outColor;
    vec4 light = texture(lightmapSampler, outLight);

    fragColor = texture;
}
