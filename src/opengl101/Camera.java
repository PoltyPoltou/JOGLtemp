package opengl101;

import java.awt.event.*;
import java.lang.Math;

import org.joml.*;

import inputs.*;

public class Camera implements Runnable {
	private float speedTranslation = 0.05f, speedRotation = 0.1f;
	private boolean up, down, left, right, front, back;
	private Vector3f upV, downV, leftV, rightV, frontV, backV;
	private double Rx, Ry, Rz;
	private static Vector3f upDirection = new Vector3f(0, 1, 0);
	private Matrix4f lookAt;
	private Vector3f position, target, frontDirection, reset;
	private KeyboardInput keyboard;
	private Thread thread;
	private MouseLocker mouse;

	public Camera(Vector3f pos, Vector3f target, MouseLocker mouse) {
		this.frontDirection = new Vector3f().add(target).sub(pos).normalize();
		this.position = new Vector3f(pos);
		this.reset = new Vector3f(pos);
		this.target = new Vector3f(target);
		this.keyboard = new KeyboardInput();
		this.mouse = mouse;
		thread = new Thread(this);
		thread.start();
	}

	private void move() {

		if (up)
			this.translate(upV);
		if (down)
			this.translate(downV);

		if (right)
			this.translate(rightV);
		if (left)
			this.translate(leftV);

		if (back)
			this.translate(backV);
		if (front)
			this.translate(frontV);
	}

	private void rotate() {
		Vector3f v = new Vector3f(frontDirection);
		Matrix3d m = new Matrix3d();
		if (Rx != 0 || Ry != 0 || Rz != 0) {
			m.rotateXYZ(Rx, Ry, Rz);
			frontDirection = v.mul(m);
			updateDirections();
		}
	}

	private void processKey() {
		up = keyboard.isPressed(' ');
		down = keyboard.isPressed(KeyEvent.VK_CONTROL);
		left = keyboard.isPressed('q');
		right = keyboard.isPressed('d');
		front = keyboard.isPressed('z');
		back = keyboard.isPressed('s');
	}

	private void processMouse() {
		MouseEvent m = mouse.getMousePos();
		if (m != null && mouse.getLock()) {
			// Moving in X axis means a Y axis rotation
			Ry = -Math.toRadians(m.getX() - m.getComponent().getWidth() / 2) * speedRotation;
			Rx = -Math.toRadians(m.getY() - m.getComponent().getHeight() / 2) * speedRotation;
		} else {
			Rx = 0;
			Ry = 0;
			Rz = 0;
		}
		mouse.centerMouse();
	}

	@Override
	public void run() {
		while (true) {
			processKey();
			processMouse();
			move();
			rotate();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("Thread Camera " + this.getClass().getName() + "a été interrompu");
			}
		}
	}

	// called every time target or position is modified
	private void updateDirections() {
		upV = new Vector3f(upDirection).mul(speedTranslation);
		downV = new Vector3f(upV).negate();
		rightV = new Vector3f(frontDirection).cross(upDirection).mul(speedTranslation);
		leftV = new Vector3f(rightV).negate();
		frontV = new Vector3f(frontDirection).mul(speedTranslation);
		backV = new Vector3f(frontV).negate();
	}

	public void translate(Vector3f v) {
		position.add(v);
	}

	synchronized public Matrix4f getLookAt() {
		return lookAt = new Matrix4f().lookAt(position, new Vector3f().add(position).add(frontDirection), upDirection);
	}

	public Matrix4f getLastLook() {
		return lookAt;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getTarget() {
		return target;
	}

	public void setCameraPos(Vector3f cameraPos) {
		this.position = cameraPos;
	}

	public void setPosX(float x) {
		this.position.x = x;
		setFront();
		updateDirections();
	}

	public void setPosY(float y) {
		this.position.y = y;
		setFront();
		updateDirections();
	}

	public void setPosZ(float z) {
		this.position.z = z;
		setFront();
		updateDirections();
	}

	public void setTargetX(float x) {
		this.target.x = x;
		setFront();
		updateDirections();
	}

	public void setTargetY(float y) {
		this.target.y = y;
		setFront();
		updateDirections();
	}

	public void setTargetZ(float z) {
		this.target.z = z;
		setFront();
		updateDirections();
	}

	public void setTarget(Vector3f cameraTarget) {
		target = new Vector3f(cameraTarget);
		setFront();
		updateDirections();
	}

	private void setFront() {
		frontDirection = new Vector3f().add(target).sub(position).normalize();
	}

	public KeyboardInput getKeyboard() {
		return keyboard;
	}

	public MouseLocker getMouseLocker() {
		return mouse;
	}

	public void reset() {
		position.set(reset);
	}

}
