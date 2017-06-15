package graphicEngine.lights;

import org.joml.*;

import com.jogamp.opengl.*;

public class DirLightShadow extends DirLight {
	public DirLightShadow(GL4 gl, Vector3f direction, LightProperties l) {
		super(gl, direction, l);
	}

	public Matrix4f getLightSpaceTransform(float nearPane, float farPane) {
		Matrix4f lightProjection = new Matrix4f().ortho(-10, 10, -10, 10, nearPane, farPane);
		Matrix4f lightView = new Matrix4f().lookAt(getDirection().negate(), new Vector3f(0), new Vector3f(0, 1, 0));
		return lightProjection.mul(lightView);
	}
}
