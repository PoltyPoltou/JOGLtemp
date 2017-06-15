package graphicEngine;

import com.jogamp.opengl.*;

import opengl101.*;

public class DepthTexture extends Depth {

	public DepthTexture(GL4 g, int height, int width, String name) {
		super(g, height, width, name);
		faces = 1;
		gl.glBindTexture(GL4.GL_TEXTURE_2D, getId());
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT, width, height, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}

	@Override
	public void bind() {
		gl.glActiveTexture(GL4.GL_TEXTURE10);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, getId());
	}

	@Override
	public void bind(ShaderProgram s) {
		s.bind();
		gl.glActiveTexture(GL4.GL_TEXTURE10);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, getId());
		gl.glUniform1i(gl.glGetUniformLocation(s.getPgrmId(), unitName), 10);
	}

	@Override
	public void bind(ShaderProgram s, String name) {
		s.bind();
		gl.glActiveTexture(GL4.GL_TEXTURE0);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, getId());
		gl.glUniform1i(gl.glGetUniformLocation(s.getPgrmId(), name), 0);
	}

	@Override
	public void bind(ShaderProgram s, String name, int textureUnit) {
		s.bind();
		gl.glActiveTexture(GL4.GL_TEXTURE0 + textureUnit);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, getId());
		gl.glUniform1i(gl.glGetUniformLocation(s.getPgrmId(), name), textureUnit);
	}
}
