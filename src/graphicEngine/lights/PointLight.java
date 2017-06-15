package graphicEngine.lights;

import org.joml.*;

import com.jogamp.opengl.*;

import opengl101.*;

public class PointLight extends Light {
	private static int lastPointLightIndex = -1;
	private Vector3f position;

	public PointLight() {}

	public PointLight(GL4 gl, Vector3f position, LightProperties l) {
		super(gl, l);
		this.position = position;
		index = ++lastPointLightIndex;
	}

	public void loadParameters(ShaderProgram s) {
		super.loadParameters(s, "pointLights[" + index + "]");
		s.setVec3("pointLights[" + index + "].position", position);
	}

	public Vector3f getPosition() {
		return new Vector3f(position);
	}
}
