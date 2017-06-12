package graphicEngine;

import org.joml.*;

import com.jogamp.opengl.*;

import opengl101.*;

public class PointLight extends Light {
	private static int lastPointLightIndex = -1;
	private Vector3f position;

	public PointLight(GL4 gl, Vector3f position, Vector3f color, Vector3f attenuation, Vector3f intensity) {

		super(gl, color, attenuation, intensity);
		this.position = position;
		index = ++lastPointLightIndex;
	}

	public PointLight(GL4 gl, Vector3f position, Vector3f color, Vector3f attenuation, Vector3f intensity, int index) {

		super(gl, color, attenuation, intensity);
		this.position = position;
		this.index = index;
	}

	public void loadParameters(ShaderProgram s) {
		super.loadParameters(s, "pointLights[" + index + "]");
		s.setVec3("pointLights[" + index + "].position", position);
	}
}
