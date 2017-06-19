package graphicEngine;

import java.nio.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

import opengl101.*;

public abstract class Depth {
	protected GL4 gl;
	private IntBuffer id;
	protected String unitName;
	protected int height, width;
	protected int faces;

	public Depth() {}

	public Depth(GL4 g, int height, int width, String name) {
		gl = g;
		this.height = height;
		this.width = width;
		unitName = name;
		id = GLBuffers.newDirectIntBuffer(1);
		gl.glGenTextures(1, id);
	}

	public abstract void bind();

	public abstract void bind(ShaderProgram s);

	public abstract void bind(ShaderProgram s, String name);

	public abstract void bind(ShaderProgram s, String name, int textureUnit);

	public abstract void bindToFrameBuffer();

	public int getId() {
		return id.get(0);
	}

	public IntBuffer getIdBuffer() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getFaces() {
		return faces;
	}
}
