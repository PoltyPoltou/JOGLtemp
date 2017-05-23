package opengl101;

import java.awt.*;
import java.awt.event.*;

import org.joml.*;

public class Camera implements Runnable {
	private float speedTranslation = 0.05f, speedRotation = 0.01f;
	private boolean up, down, left, right, front, back;
	private boolean Rx, Ry, Rz;
	private byte RxSign, RySign, RzSign;
	private static Vector3f upDirection = new Vector3f(0, 1, 0);
	private Matrix4f lookAt;
	private Vector3f position, target, frontDirection, reset;
	private KeyboardInput keyboard;
	private Thread thread;

	public Camera(Vector3f pos, Vector3f target) {
		this.frontDirection = new Vector3f().add(target).sub(pos).normalize();
		this.position = new Vector3f(pos);
		this.reset = new Vector3f(pos);
		this.target = new Vector3f(target);
		this.keyboard = new KeyboardInput();
		thread = new Thread(this);
		thread.start();
	}

	synchronized private void move() {
		if (up)
			this.translate(new Vector3f(0, speedTranslation, 0), true);
		if (down)
			this.translate(new Vector3f(0, -speedTranslation, 0), true);

		if (right)
			this.translate(new Vector3f(speedTranslation, 0, 0), true);
		if (left)
			this.translate(new Vector3f(-speedTranslation, 0, 0), true);

		if (back)
			this.translate(new Vector3f(0, 0, speedTranslation), true);
		if (front)
			this.translate(new Vector3f(0, 0, -speedTranslation), true);

		if (Ry)
			this.rotate(frontDirection);
		if (Rx)
			;
	}

	private void rotate(Vector3f v, int x, int y, int z) {
		v.add()
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

				case KeyEvent.VK_A :
					Ry = trigger;
					RySign = 1;
					break;
				case KeyEvent.VK_E :
					Ry = trigger;
					RySign = -1;
					break;

				case KeyEvent.VK_F :
					Rx = trigger;
					RxSign = 1;
					break;
				case KeyEvent.VK_G :
					Rx = trigger;
					RxSign = -1;
					break;

				case KeyEvent.VK_R :
					reset();
					break;

				default :
					break;
			}
		} else
			keyboard.takeType();
	}

	@Override
	public void run() {
		while (true) {
			this.processKey();
			this.move();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("Thread Camera " + this.getClass().getName() + "a été interrompu");
			}
		}
	}

	public void translate(Vector3f v, boolean moveTarget) {
		position.add(v);
		if (moveTarget)
			target.add(v);
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

	public void setCameraTarget(Vector3f cameraTarget) {
		this.target = cameraTarget;
	}

	public void setTargetX(float x) {
		this.target.x = x;
	}

	public void setTargetY(float y) {
		this.target.y = y;
	}

	public void setTargetZ(float z) {
		this.target.z = z;
	}

	public KeyboardInput getKeyboard() {
		return keyboard;
	}

	public void reset() {
		position.set(reset);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		new Robot()

	}

}
