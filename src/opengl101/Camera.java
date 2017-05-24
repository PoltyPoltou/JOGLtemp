package opengl101;

import java.awt.event.*;
import java.lang.Math;

import org.joml.*;

public class Camera implements Runnable {
	private float speedTranslation = 0.05f, speedRotation = 0.1f;
	private boolean up, down, left, right, front, back;
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
			this.translate(new Vector3f(upDirection).mul(speedTranslation));
		if (down)
			this.translate(new Vector3f(upDirection).mul(-speedTranslation));

		if (right)
			this.translate(new Vector3f(frontDirection).cross(upDirection).mul(speedTranslation));
		if (left)
			this.translate(new Vector3f(frontDirection).cross(upDirection).mul(-speedTranslation));

		if (back)
			this.translate(new Vector3f(frontDirection).mul(-speedTranslation));
		if (front)
			this.translate(new Vector3f(frontDirection).mul(speedTranslation));
	}

	private void rotate() {
		Quaterniond q = new Quaterniond();
		Vector3f v = new Vector3f(frontDirection);
		Matrix3f m = new Matrix3f();
		if (Rx != 0 || Ry != 0 || Rz != 0) {
			q.rotateX(Rx);
			q.rotateY(Ry);
			q.get(m);
			frontDirection = v.mul(m);
		}
	}

	private void processKey() {
		KeyEvent e = keyboard.take();
		if (e != null) {
			boolean trigger = keyboard.takeType();
			switch (e.getKeyCode()) {
				case KeyEvent.VK_S :
					back = trigger;
					break;
				case KeyEvent.VK_Z :
					front = trigger;
					break;

				case KeyEvent.VK_SPACE :
					up = trigger;
					break;
				case KeyEvent.VK_CONTROL :
					down = trigger;
					break;

				case KeyEvent.VK_D :
					right = trigger;
					break;
				case KeyEvent.VK_Q :
					left = trigger;
					break;

				case KeyEvent.VK_R :
					reset();
					break;
				case KeyEvent.VK_M :
					mouse.setLock(false);
					break;
				case KeyEvent.VK_L :
					mouse.setLock(true);
					break;
				default :
					break;
			}
		} else
			keyboard.takeType();
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
				Thread.sleep(1);
			} catch (InterruptedException e) {
				System.out.println("Thread Camera " + this.getClass().getName() + "a été interrompu");
			}
		}
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
	}

	public void setPosY(float y) {
		this.position.y = y;
	}

	public void setPosZ(float z) {
		this.position.z = z;
	}

	public void setTargetX(float x) {
		this.target.x = x;
		setTarget();
	}

	public void setTargetY(float y) {
		this.target.y = y;
		setTarget();
	}

	public void setTargetZ(float z) {
		this.target.z = z;
		setTarget();
	}

	public void setTarget(Vector3f cameraTarget) {
		target = new Vector3f(cameraTarget);
		setTarget();
	}

	public void setTarget() {
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
