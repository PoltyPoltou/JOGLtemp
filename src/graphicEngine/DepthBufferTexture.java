package graphicEngine;

import java.nio.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

import opengl101.*;

public class DepthBufferTexture {
	private GL4 gl;
	private IntBuffer textureId;
	private String unitName[];
	private int height, width;

	public DepthBufferTexture(GL4 g, int height, int width, String name) {
		gl = g;
		this.height = height;
		this.width = width;
		//format off
		unitName = new String[] {name};
		//format on
		textureId = GLBuffers.newDirectIntBuffer(1);
		gl.glGenTextures(1, textureId);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, textureId.get(0));
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT, width, height, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}

	public DepthBufferTexture(GL4 g, int height, int width, String[] name) {
		gl = g;
		//format off
		unitName = name;
		//format on
		textureId = GLBuffers.newDirectIntBuffer(name.length);
		gl.glGenTextures(name.length, textureId);
		for (int i = 0; i < name.length; i++) {
			gl.glBindTexture(GL4.GL_TEXTURE_2D, textureId.get(i));
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT, height, width, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
			gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		}
	}

	public void bind() {
		gl.glActiveTexture(GL4.GL_TEXTURE31);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, getId());
	}

	public void bind(ShaderProgram s) {
		s.bind();
		gl.glActiveTexture(GL4.GL_TEXTURE31);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, getId());
		gl.glUniform1i(gl.glGetUniformLocation(s.getPgrmId(), unitName[0]), 31);
	}

	public void bind(ShaderProgram s, String name) {
		s.bind();
		gl.glActiveTexture(GL4.GL_TEXTURE31);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, getId());
		gl.glUniform1i(gl.glGetUniformLocation(s.getPgrmId(), name), 31);
	}

	public int getId() {
		return textureId.get(0);
	}

	public int getId(int index) {
		return textureId.get(index);
	}

	public IntBuffer getIdBuffer() {
		return textureId;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
