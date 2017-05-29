package inputs;

import java.awt.event.*;
import java.util.*;

public class KeyboardInput extends KeyAdapter {

	private HashMap<Integer, Boolean> mapKey;
	private HashMap<Character, Boolean> mapKeyChar;

	public KeyboardInput() {
		mapKey = new HashMap<>();
		mapKeyChar = new HashMap<>();
	}

	public HashMap<Integer, Boolean> getMap() {
		return mapKey;
	}

	public HashMap<Character, Boolean> getMapChar() {
		return mapKeyChar;
	}

	public boolean isPressed(int keyCode) {
		if (mapKey.containsKey(keyCode))
			return mapKey.get(keyCode);
		else
			return false;
	}

	public boolean isPressed(char name) {
		if (mapKeyChar.containsKey(name))
			return mapKeyChar.get(name);
		else
			return false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		mapKey.put(e.getKeyCode(), true);
		mapKeyChar.put(e.getKeyChar(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		mapKey.put(e.getKeyCode(), false);
		mapKeyChar.put(e.getKeyChar(), false);
	}
}
