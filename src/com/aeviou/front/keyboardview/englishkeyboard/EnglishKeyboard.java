package com.aeviou.front.keyboardview.englishkeyboard;

import android.graphics.Canvas;
import android.os.Handler;
import android.view.KeyEvent;

import com.aeviou.Util.Constants;
import com.aeviou.Util.Globals;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKey;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKeyboard;

public class EnglishKeyboard extends AbstractKeyboard {

	protected static int LONG_PRESS_TIME = 100;

	protected EnglishKey lastKey = null;
	protected boolean shifted = false;
	protected long lastTime = 0;
	protected long timeSpan = 1000;
	protected String subtext = "";

	protected final Handler _handler = new Handler();
	protected Runnable _longPressed = new Runnable() {
		public void run() {
			if (lastKey.getName().equals("delete")) {
				Globals.service.getCurrentInputConnection().sendKeyEvent( new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
				Globals.service.getCurrentInputConnection().sendKeyEvent( new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
				_handler.postDelayed(this, LONG_PRESS_TIME);
			} else if (lastKey.getId() < 26) {
				subtext = lastKey.getSubtext();
			}
		}
	};

	public EnglishKeyboard(EnglishKey[] keys) {
		this.keys = keys;
	}

	@Override
	public void draw(Canvas Cvs) {
		// TODO Auto-generated method stub
		for (int i = 0; i < keys.length; i++) {
			keys[i].draw(Cvs);
		}
	} 

	@Override
	public boolean onTouch(float x, float y) {
		// TODO Auto-generated method stub
		int id = getEnglishKeyByPointOnTouch(x, y);
		if (lastKey != null) {
			lastKey.setStatus(Constants.KEY_STATUS_NORMAL);
		}
		lastKey = (EnglishKey) keys[id];
		_handler.postDelayed(_longPressed, LONG_PRESS_TIME * 3);
		keys[id].setStatus(Constants.KEY_STATUS_SELECTED);
		return true;
	}

	@Override
	public boolean onMove(float x, float y) {
		// TODO Auto-generated method stub
		if (x < lastKey.getLeftTopX() || x > lastKey.getLeftTopX() + lastKey.getWidth() || y < lastKey.getLeftTopY() || y > lastKey.getLeftTopY() + lastKey.getWidth()) {
			int id = getEnglishKeyByPointOnTouch(x, y);
			lastKey.setStatus(Constants.KEY_STATUS_NORMAL);
			lastKey = (EnglishKey) keys[id];
			keys[id].setStatus(Constants.KEY_STATUS_SELECTED);
			_handler.removeCallbacks(_longPressed);
		}

		return true;
	}

	@Override
	public String onRelease(float x, float y) {
		// TODO Auto-generated method stub
		int id = getEnglishKeyByPointOnTouch(x, y);
		if (lastKey != null) {
			lastKey.setStatus(Constants.KEY_STATUS_NORMAL);
		}
		lastKey = (EnglishKey) keys[id];
		_handler.removeCallbacks(_longPressed);
		if (lastKey.getName().equals("delete")) {
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
		} else if (lastKey.getName().equals("enter")) {
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
		} else if (lastKey.getName().equals("space")) {
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));
		} else if (lastKey.getName().equals("at")) {
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_AT));
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_AT));
		} else if (lastKey.getName().equals("comma")) {
			Globals.service.getCurrentInputConnection().sendKeyEvent(
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_COMMA));
			Globals.service.getCurrentInputConnection().sendKeyEvent(
					new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_COMMA));
		} else if (lastKey.getName().equals("dot")) {
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_PERIOD));
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_PERIOD));
		} else if (lastKey.getName().equals("ch")) {
			clearKeyboard();
			return "ch";
		} else if (lastKey.getName().equals("num_sym")) {
			clearKeyboard();
			return "symbols";
		} else if (lastKey.getName().equals("setting")) {
			clearKeyboard();
			return "setting";
		} else if (lastKey.getName().equals("caps")) {
			applyShifted();
		} else if (lastKey.getId() < 26) {
			if (subtext == null || subtext.equals("")) {
				String text = lastKey.getName();
				if (shifted) {
					text = String.valueOf((char) ((lastKey.getName().toCharArray())[0] - 32));
				}
				Globals.service.getCurrentInputConnection().commitText(text, 1);
			} else {
				Globals.service.getCurrentInputConnection().commitText(subtext, 1);
				subtext = "";
			}
		}

		clearKeyboard();

		return null;
	}

	private void clearKeyboard() {
		for (int i = 0; i < keys.length; i++) {
			keys[i].setStatus(Constants.KEY_STATUS_NORMAL);
		}
	}

	private void applyShifted() {
		shifted = !shifted;
		if (shifted) {
			for (AbstractKey key : keys) {
				if (key.getId() < 26) {
					key.setType("upper");
				}
			}
		} else {
			for (AbstractKey key : keys) {
				if (key.getId() < 26) {
					key.setType("lower");
				}
			}
		}
	}

	private int getEnglishKeyByPointOnTouch(float x, float y) {
		int englishKeyID = -1;
		float shortest = 100000000;

		// get special key: space
		EnglishKey specialKey = (EnglishKey) keys[keys.length - 1];
		for (int i = keys.length - 1; i >= 0; i--) {
			if (keys[i].getName().equals("space")) {
				specialKey = (EnglishKey) keys[i];
				break;
			}
		}

		if (x >= specialKey.getLeftTopX() && x <= specialKey.getLeftTopX() + specialKey.getWidth() && y >= specialKey.getLeftTopY() && y <= specialKey.getLeftTopY() + specialKey.getHeight()){
			englishKeyID = specialKey.getId();
		} else {
			for (int i = 0; i < keys.length; i++) {
				float distance = (keys[i].getCenterX() - x) * (keys[i].getCenterX() - x) + (keys[i].getCenterY() - y) * (keys[i].getCenterY() - y);
				if (distance < shortest) {
					shortest = distance;
					englishKeyID = i;
				}
			}
		}

		return englishKeyID;
	}

}
