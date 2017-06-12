package graphicEngine;

import org.joml.*;

import com.jogamp.opengl.*;

import opengl101.*;

public class DirLight extends Light {
	private Vector3f direction;

	public DirLight(GL4 gl, Vector3f direction, Vector3f color, Vector3f attenuation, Vector3f intensity) {
		super(gl, color, attenuation, intensity);
		this.direction = direction;
		drawable = false;
	}

	public DirLight(GL4 gl, Vector3f direction, Vector3f color, Vector3f intensity) {
		super(gl, color, new Vector3f(1, 0, 0), intensity);
		this.direction = direction;
		drawable = false;
	}

	public void loadParameters(ShaderProgram s) {
		super.loadParameters(s, "dirLight");
		s.setVec3("dirLight.direction", direction);

	}

}
