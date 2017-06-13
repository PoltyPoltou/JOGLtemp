package graphicEngine;

import java.nio.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

import opengl101.*;

public class ShadowDepthFrameBuffer<TextureType extends Depth> {
	private IntBuffer FBO;
	private TextureType depthTexture;
	private ShaderProgram shader;
	private GL4 gl;

	public ShadowDepthFrameBuffer(GL4 gl, TextureType t) {
		if (t.getFaces() == 1)
			shader = new ShaderProgram(gl, "shadowDirLight.vertex", "shadowDirLight.fragment");// dirLight
		if (t.getFaces() == 6)
			shader = new ShaderProgram(gl, "shadowPointLight.vertex", "shadowPointLight.fragment", "shadowPointLight.geometry");// PointLight
		this.gl = gl;
		genBuffers();
		depthTexture = t;
		attachDepthTexture(depthTexture);
		bind();
		gl.glEnable(GL4.GL_DEPTH_TEST);
	}

	private void genBuffers() {
		FBO = GLBuffers.newDirectIntBuffer(1);
		gl.glGenFramebuffers(1, FBO);
	}

	public void bind() {
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, FBO.get(0));
		gl.glCullFace(GL4.GL_FRONT);
	}

	public void unBind() {
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		gl.glCullFace(GL4.GL_BACK);
	}

	public void bindDepthTexture(ShaderProgram s, String name) {// bind to a different shader to make shadows
		depthTexture.bind(s, name);
	}

	public void bindDepthTexture(ShaderProgram s) {// bind to a different shader to make shadows
		depthTexture.bind(s);
	}

	public void bindDepthTexture() {// bind to the depth texture
		depthTexture.bind(shader);
	}

	public void clearBuffer() {
		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT);
	}

	private void attachDepthTexture(TextureType t) {
		bind();
		t.bind();
		gl.glFramebufferTexture2D(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, GL4.GL_TEXTURE_2D, t.getId(), 0);
		gl.glDrawBuffer(GL4.GL_NONE);
		gl.glReadBuffer(GL4.GL_NONE);
		unBind();
	}

	public ShaderProgram getShader() {
		return shader;
	}
}
