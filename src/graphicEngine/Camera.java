package graphicEngine;

import org.joml.*;

public class Camera {
	private Vector3f position, target, xAxis, yAxis, zAxis;// direction positive yAxis is not updated
	private Matrix4f lookAt;

	protected Camera(Vector3f position, Vector3f target) {
		this.position = position;
		this.target = target;
		yAxis = new Vector3f(0, 1, 0);
		updateAxis();
	}

	private void updateAxis() {
		xAxis.set(target).sub(position);
		yAxis.cross(xAxis, zAxis);
	}

	protected Matrix4f getLookAt() {
		lookAt.setLookAt(position, new Vector3f(position).add(xAxis), yAxis);
		return lookAt;
	}

}
