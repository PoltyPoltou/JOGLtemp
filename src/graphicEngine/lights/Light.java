package graphicEngine.lights;

import com.jogamp.opengl.*;

import graphicEngine.*;
import opengl101.*;

public abstract class Light extends Model {

	protected LightProperties lightProp;
	protected int index;// shader index for uniform call

	public Light() {}

	public Light(GL4 gl) {
		super(gl);
		lightProp = new LightProperties();
	}

	public Light(GL4 gl, LightProperties prop) {
		super(gl);
		lightProp = prop;
	}

	protected void loadParameters(ShaderProgram s, String uniformName) {
		s.setVec3(uniformName + ".color", lightProp.color);
		s.setVec3(uniformName + ".attenuation", lightProp.attenuation);
		s.setVec3(uniformName + ".intensity", lightProp.intensity);
	}

}
