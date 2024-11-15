#version 450

in vec4 outColor;
in vec2 outTexturePos;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    vec4 texureColor = (outTexturePos.x >= 0 && outTexturePos.y >= 0) ?
        texture(textureSampler, outTexturePos) :
        vec4(0,0,0,0);
    fragColor = texureColor + outColor * (1 - texureColor.a);
}