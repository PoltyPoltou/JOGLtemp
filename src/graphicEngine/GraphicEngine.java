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

	public Model createModel(ShaderProgram pgrm, Texture text, String objFile) {
		Model m = new Model(objFile, gl, text, pgrm);
		return m;
	}

	public Camera createCamera(Vector3f pos, Vector3f targ) {
		Camera c = new Camera(pos, targ);
		return c;
	}
}
