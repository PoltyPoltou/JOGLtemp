package opengl101;

import java.awt.event.*;
import java.util.*;

public class KeyboardInput extends KeyAdapter {

	private LinkedList<KeyEvent> input;
	private LinkedList<Boolean> type;

	public KeyboardInput() {
		input = new LinkedList<>();
		type = new LinkedList<>();
	}

	public Queue<KeyEvent> getQueue() {
		return input;
	}

	public KeyEvent peek() {
		return input.peekFirst();
	}

	public KeyEvent take() {
		return input.pollFirst();
	}

	public Boolean peekType() {
		return type.peekFirst();
	}

	public Boolean takeType() {
		return type.pollFirst();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		input.addLast(e);
		type.addLast(true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		input.addLast(e);
		type.addLast(false);
	}
}
