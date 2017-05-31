#version 450


layout (location = 0) in vec3 in_position;


uniform mat4 uni_model;
uniform mat4 uni_view;
uniform mat4 uni_proj;


void main()
{
    gl_Position = uni_proj*uni_view*uni_model*vec4(in_position,1.0);
}