package opengl101;

import java.awt.*;
import java.awt.event.*;

public class MouseLocker implements MouseMotionListener {

	private Robot robot;
	private boolean lock;
	private MouseEvent mouse;
	private Component window;

	public MouseLocker(Component mainWindow) {
		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			System.out.println("robot loading failed at " + this.getClass().getName());
		}
		this.window = mainWindow;
		this.lock = true;
	}

	public MouseEvent getMousePos() {
		return mouse;
	}

	public boolean centerMouse() {
		if (lock) {
			robot.mouseMove((int) (window.getLocationOnScreen().getX() + window.getWidth() / 2), (int) (window.getLocationOnScreen().getY() + window.getHeight() / 2));
			return true;
		} else
			return false;
	}

	public void setLock(boolean b) {
		lock = b;
	}

	public void swapLock() {
		lock = !lock;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse = e;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
