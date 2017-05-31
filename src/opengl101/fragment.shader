#version 450

in vec2 texCoord;

out vec4 out_Color;

uniform sampler2D uni_Texture1;

void main()
{
    out_Color= texture(uni_Texture1, texCoord);
}