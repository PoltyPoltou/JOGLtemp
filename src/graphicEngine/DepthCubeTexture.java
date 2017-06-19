package graphicEngine;

import com.jogamp.opengl.*;

import opengl101.*;

public class DepthCubeTexture extends Depth {

	public DepthCubeTexture(GL4 g, int height, int width, String name) {
		super(g, height, width, name);
		faces = 6;
		gl.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, getId());
		// setup each face of cubeMap
		for (int i = 0; i < 6; ++i)
			gl.glTexImage2D(GL4.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL4.GL_DEPTH_COMPONENT, width, height, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_R, GL4.GL_CLAMP_TO_EDGE);
		gl.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, 0);
	}

	@Override
	public void bind() {
		gl.glActiveTexture(GL4.GL_TEXTURE3);
		gl.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, getId());
	}

	@Override
	public void bind(ShaderProgram s) {
		s.bind();
		gl.glActiveTexture(GL4.GL_TEXTURE3);
		gl.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, getId());
		gl.glUniform1i(gl.glGetUniformLocation(s.getPgrmId(), unitName), 3);
	}

	@Override
	public void bind(ShaderProgram s, String name) {
		s.bind();
		gl.glActiveTexture(GL4.GL_TEXTURE3);
		gl.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, getId());
		gl.glUniform1i(gl.glGetUniformLocation(s.getPgrmId(), name), 3);
	}

	@Override
	public void bind(ShaderProgram s, String name, int textureUnit) {
		s.bind();
		gl.glActiveTexture(GL4.GL_TEXTURE0 + textureUnit);
		gl.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, getId());
		gl.glUniform1i(gl.glGetUniformLocation(s.getPgrmId(), name), textureUnit);
	}

	@Override
	public void bindToFrameBuffer() {
		gl.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, getId(), 0);
	}
}
