package graphicEngine.lights;

import java.lang.Math;

import org.joml.*;

import com.jogamp.opengl.*;

import graphicEngine.*;

public class PointLightShadow extends PointLight {

	public PointLightShadow(GL4 gl, Vector3f position, LightProperties l) {
		super(gl, position, l);
	}

	public Matrix4f[] getLightSpaceTransform(DepthCubeTexture t, float nearPane, float farPane) {
		Matrix4f[] lightSpaceTransform = new Matrix4f[6];
		float aspect = t.getWidth() / t.getHeight();
		Matrix4f shadowProjection = new Matrix4f().setPerspective((float) (Math.PI / 2), aspect, nearPane, farPane);
		//format off
		// create 6 space transform for the six face of the cubeMap
		lightSpaceTransform[0] = shadowProjection.mul(new Matrix4f().lookAt(getPosition(), getPosition().add(new Vector3f( 1, 0, 0)), new Vector3f(0, -1,  0)));
		lightSpaceTransform[1] = shadowProjection.mul(new Matrix4f().lookAt(getPosition(), getPosition().add(new Vector3f(-1, 0, 0)), new Vector3f(0, -1,  0)));
		lightSpaceTransform[2] = shadowProjection.mul(new Matrix4f().lookAt(getPosition(), getPosition().add(new Vector3f(0,  1, 0)), new Vector3f(0,  0,  1)));
		lightSpaceTransform[3] = shadowProjection.mul(new Matrix4f().lookAt(getPosition(), getPosition().add(new Vector3f(0, -1, 0)), new Vector3f(0,  0, -1)));
		lightSpaceTransform[4] = shadowProjection.mul(new Matrix4f().lookAt(getPosition(), getPosition().add(new Vector3f(0, 0,  1)), new Vector3f(0, -1,  0)));
		lightSpaceTransform[5] = shadowProjection.mul(new Matrix4f().lookAt(getPosition(), getPosition().add(new Vector3f(0, 0, -1)), new Vector3f(0, -1,  0)));
		//format on
		return lightSpaceTransform;
	}
}
