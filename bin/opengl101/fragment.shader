#version 450

in vec2 texCoord;
out vec4 out_Color;

uniform sampler2D uni_Texture1;
uniform sampler2D uni_Texture2;

void main()
{
    out_Color =  out_Color = mix(texture(uni_Texture1, vec2(0.5,0.5)),texture(uni_Texture2, vec2(0.5,0.5)),0.2);
}