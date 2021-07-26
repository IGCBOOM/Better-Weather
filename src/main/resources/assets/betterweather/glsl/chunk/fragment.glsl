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
    float red = outColor.x;
    float green = outColor.y;
    float blue = outColor.z;

    vec4 color = vec4(red, green, blue, outColor.w);

    if (green > red) {
        if (green > blue) {
            float multipliedRed = red * 0.3;
            float multipliedGreen = green * 0.6;
            float multipliedBlue = blue * 0.1;

            float gray = multipliedRed + multipliedGreen + multipliedBlue;

            color = vec4(gray, gray, gray, color.w);
        }
    }

    fragColor = texture(textureSampler, outUv) * color;
}
