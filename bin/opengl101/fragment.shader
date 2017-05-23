#version 450

in vec4 color;
in vec2 texCoord;
out vec4 out_Color;

uniform sampler2D uni_Texture1;
uniform sampler2D uni_Texture2;

void main()
{
    out_Color = mix(texture(uni_Texture1, texCoord),texture(uni_Texture2, texCoord),0.2); 
}