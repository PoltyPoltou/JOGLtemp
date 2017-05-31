#version 450


layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_texCoord;

out vec2 texCoord;


uniform mat4 uni_model;
uniform mat4 uni_view;
uniform mat4 uni_proj;


void main()
{
    gl_Position = uni_proj*uni_view*uni_model*vec4(in_position,1.0);
    texCoord = in_texCoord;
}