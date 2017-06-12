package graphicEngine;

import java.nio.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

import opengl101.*;

public class FrameBuffer {
	private IntBuffer FBO;
	private DepthBufferTexture color, depth, stencil;
	private ShaderProgram shadowShader;
	private GL4 gl;

	public FrameBuffer(GL4 gl, DepthBufferTexture t) {
		shadowShader = new ShaderProgram("shadow.vertex", "shadow.fragment", gl);
		this.gl = gl;
		genBuffers();
		depth = t;
		attachDepthTexture(depth);
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
		depth.bind(s, name);
	}

	public void bindDepthTexture(ShaderProgram s) {// bind to a different shader to make shadows
		depth.bind(s);
	}

	public void bindDepthTexture() {// bind to the depth texture
		depth.bind(shadowShader);
	}

	public void clearBuffer() {
		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT);
	}

	private void attachDepthTexture(DepthBufferTexture t) {
		bind();
		t.bind();
		gl.glFramebufferTexture2D(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, GL4.GL_TEXTURE_2D, t.getId(), 0);
		gl.glDrawBuffer(GL4.GL_NONE);
		gl.glReadBuffer(GL4.GL_NONE);
		unBind();
	}

	public ShaderProgram getShader() {
		return shadowShader;
	}
}
