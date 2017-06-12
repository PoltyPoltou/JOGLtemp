package graphicEngine;

import org.joml.*;

import com.jogamp.opengl.*;

import opengl101.*;

public abstract class Light extends Model {

	protected Vector3f color;
	protected Vector3f attenuation;// Constant / linear / quadratic
	protected Vector3f intensity;// ambient / diffuse / specular
	protected int index;// shader index for uniform call

	public Light(GL4 gl) {
		super(gl);
		color = new Vector3f();
		attenuation = new Vector3f();
		intensity = new Vector3f();
	}

	public Light(GL4 gl, Vector3f c, Vector3f a, Vector3f i) {
		super(gl);
		color = c;
		attenuation = a;
		intensity = i;
	}

	protected void loadParameters(ShaderProgram s, String uniformName) {
		s.setVec3(uniformName + ".color", color);
		s.setVec3(uniformName + ".attenuation", attenuation);
		s.setVec3(uniformName + ".intensity", intensity);
	}

}
