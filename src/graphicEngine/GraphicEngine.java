package graphicEngine;

import org.joml.*;

import com.jogamp.opengl.*;

import opengl101.*;

public class GraphicEngine {
	private Scene scene;
	private Camera camera;
	private Renderer render;
	private GL4 gl;

	public GraphicEngine() {

	}

	public NonLightObjects createModel(ShaderProgram pgrm, Texture text, String objFile) {
		NonLightObjects m = new NonLightObjects(objFile, gl, text, pgrm);
		return m;
	}

	public Camera createCamera(Vector3f pos, Vector3f targ) {
		Camera c = new Camera(pos, targ);
		return c;
	}

	public DirLight createDirLight(Vector3f pos, Vector3f color, String objFile) {
		DirLight l = new DirLight(objFile, pos, color);
		return l;
	}

	public void DepthTest(boolean b) {
		gl.glEnable(GL4.GL_DEPTH_TEST);
	}

	// can improve perfs
	public void CullFace(boolean b) {
		gl.glEnable(GL4.GL_CULL_FACE);
	}
}
