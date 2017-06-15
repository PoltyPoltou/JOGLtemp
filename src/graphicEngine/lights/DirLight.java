package graphicEngine.lights;

import org.joml.*;

import com.jogamp.opengl.*;

import opengl101.*;

public class DirLight extends Light {
	private Vector3f direction;

	public DirLight() {}

	public DirLight(GL4 gl, Vector3f direction, LightProperties l) {
		super(gl, l);
		this.direction = direction;
		drawable = false;
	}

	public void loadParameters(ShaderProgram s) {
		super.loadParameters(s, "dirLight");
		s.setVec3("dirLight.direction", direction);
	}

	public Vector3f getDirection() {
		return new Vector3f(direction);
	}

}
