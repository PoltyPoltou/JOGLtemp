package graphicEngine;

import org.joml.*;

import com.jogamp.opengl.*;

import opengl101.*;

public class NonLightObjects extends Model {
	private Matrix3f normalMatrix;
	private boolean reverseNormal = false;

	public NonLightObjects(GL4 gl, String file) {
		super(gl, file);
		normalMatrix = new Matrix3f();
		drawable = true;
	}

	public NonLightObjects(GL4 gl, String file, Texture t, ShaderProgram s) {
		super(gl, file, t, s);
		normalMatrix = new Matrix3f();
		drawable = true;
	}

	public NonLightObjects(GL4 gl, String file, Matrix4f model) {
		super(gl, file, model);
		updateNormalMatrix();
		drawable = true;
	}

	@Override
	protected void bindModel() {
		super.bindModel();
		updateNormalMatrix();
		shader.setMat3("uni_normalMatrix", normalMatrix);
	}

	private void updateNormalMatrix() {
		new Matrix4f(model).invert().transpose3x3(normalMatrix);
		if (reverseNormal)
			normalMatrix.scale(-1);
	}

	public void reverseNormal() {
		reverseNormal = !reverseNormal;
	}

}
