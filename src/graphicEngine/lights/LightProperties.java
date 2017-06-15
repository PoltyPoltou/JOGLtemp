package graphicEngine.lights;

import org.joml.*;

public class LightProperties {
	public static final Vector3f NO_ATTENUATION = new Vector3f(1, 0, 0);
	public static final Vector3f WHITE_COLOR = new Vector3f(1);
	public Vector3f color;
	public Vector3f attenuation;// Constant / linear / quadratic
	public Vector3f intensity;// ambient / diffuse / specular

	public LightProperties(Vector3f color, Vector3f attenuation, Vector3f intensity) {
		this.color = color;
		this.attenuation = attenuation;
		this.intensity = intensity;
	}

	public LightProperties() {
		this.color = new Vector3f();
		this.intensity = new Vector3f();
		this.attenuation = new Vector3f();
	}
}
