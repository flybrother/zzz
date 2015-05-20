package com.aeviou.front.keyboardview.symbolkeyboard;

import android.graphics.Canvas;
import android.os.Handler;
import android.view.KeyEvent;

import com.aeviou.Util.Constants;
import com.aeviou.Util.Globals;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKey;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKeyboard;

public class SymbolKeyboard extends AbstractKeyboard {
	protected static int LONG_PRESS_TIME = 100;
	protected SymbolKey currentKey = null;
	protected long lastTime = 0;
	protected long timeSpan = 1000;
	protected int maxPage;
	protected int curPage;
	protected String lastKeyboard = "ch";

	public SymbolKeyboard(SymbolKey[] keys) {
		this.keys = keys;
		this.curPage = 0;
	}

	protected final Handler _handler = new Handler();

	protected Runnable _longPressed = new Runnable() {
		public void run() {
			if (currentKey.getName().equals("delete")) {
				Globals.service.getCurrentInputConnection()
						.sendKeyEvent(
								new KeyEvent(KeyEvent.ACTION_DOWN,
										KeyEvent.KEYCODE_DEL));
				Globals.service.getCurrentInputConnection().sendKeyEvent(
						new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}
			_handler.postDelayed(this, LONG_PRESS_TIME);
		}
	};

	/*
	 * this draw method could be abstracted into class AbtractKeyboard if all
	 * children have same logic here
	 */
	public void draw(Canvas cvs) {
		for (int i = 0; i < keys.length; i++) {
			keys[i].draw(cvs);
		}
	}

	@Override
	public boolean onTouch(float x, float y) {
		int id = getSymbolKeyByPointOnTouch(x, y);
		currentKey = (SymbolKey) keys[id];
		if (id == 30 && curPage == 1) {
			currentKey = (SymbolKey) keys[29];
		}		
		currentKey.setStatus(Constants.KEY_STATUS_SELECTED);
		_handler.postDelayed(_longPressed, LONG_PRESS_TIME * 3);
		return true;
	}

	/*
	 * nothing to do onMove
	 */
	public boolean onMove(float x, float y) {
		if (x < currentKey.getLeftTopX()
				|| x > currentKey.getLeftTopX() + currentKey.getWidth()
				|| y < currentKey.getLeftTopY()
				|| y > currentKey.getLeftTopY() + currentKey.getWidth()) {
			int id = getSymbolKeyByPointOnTouch(x, y);
			currentKey.setStatus(Constants.KEY_STATUS_NORMAL);
			currentKey = (SymbolKey) keys[id];
			keys[id].setStatus(Constants.KEY_STATUS_SELECTED);
			_handler.removeCallbacks(_longPressed);
		}

		return false;
	}

	@Override
	public String onRelease(float x, float y) {
		// TODO Auto-generated method stub
		this.currentKey.setStatus(Constants.KEY_STATUS_NORMAL);
		_handler.removeCallbacks(_longPressed);

		if (currentKey.getName().equals("delete")) {
			Globals.service.getCurrentInputConnection().sendKeyEvent(
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
			Globals.service.getCurrentInputConnection().sendKeyEvent(
					new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
		} else if (currentKey.getName().equals("enter")) {
			Globals.service.getCurrentInputConnection().sendKeyEvent(
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
			Globals.service.getCurrentInputConnection().sendKeyEvent(
					new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
		} else if (currentKey.getName().equals("space")) {
			Globals.service.getCurrentInputConnection().sendKeyEvent(
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
			Globals.service.getCurrentInputConnection().sendKeyEvent(
					new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));
		} else if (currentKey.getName().equals("page")) {
			changePage();
		} else if (currentKey.getName().equals("return")) {
			return lastKeyboard;
		} else if (currentKey.getType().equals("text")) {
			String text = currentKey.getValue(curPage);
			Globals.service.getCurrentInputConnection().commitText(text, 1);			
		} else if (currentKey.getType().equals("pair")) {
			String text = currentKey.getValue(curPage);
			Globals.service.getCurrentInputConnection().commitText(text, 1);
			if (text.length() == 2) {
				String fulltext = (String) Globals.service.getCurrentInputConnection().getTextBeforeCursor(Integer.MAX_VALUE, 0);
				int pos = fulltext.length()-1;
				Globals.service.getCurrentInputConnection().setSelection(pos, pos);
			}
		}

		return null;
	}
	private void changePage() {
		curPage = (curPage+1) % maxPage;
		for (AbstractKey key : keys) {
			((SymbolKey)key).setCurPage(curPage+1);
		}
	}
	private int getSymbolKeyByPointOnTouch(float x, float y) {
		int symbolKeyID = -1;
		float shortest = 100000000;

		SymbolKey specialKey = (SymbolKey) keys[keys.length - 1];
		for (int i = keys.length - 1; i >= 0; i--) {
			if (keys[i].getName().equals("space")) {
				specialKey = (SymbolKey) keys[i];
				break;
			}
		}

		if (x >= specialKey.getLeftTopX()
				&& x <= specialKey.getLeftTopX() + specialKey.getWidth()
				&& y >= specialKey.getLeftTopY()
				&& y <= specialKey.getLeftTopY() + specialKey.getHeight()) {
			symbolKeyID = specialKey.getId();
			/*
			 * Log.d("EnglishKeyboard", "Touch: " + specialKey.getId());
			 * Log.d("EnglishKeyboard", "Touch: " + specialKey.getName() + x +
			 * " " + y + " " + specialKey.getLeftTopX() + " " +
			 * specialKey.getLeftTopX() + specialKey.getWidth() + " " +
			 * specialKey.getLeftTopY() + " " + specialKey.getLeftTopY() +
			 * specialKey.getHeight());
			 */
		} else {
			for (int i = 0; i < keys.length; i++) {
				float distance = (keys[i].getCenterX() - x)
						* (keys[i].getCenterX() - x)
						+ (keys[i].getCenterY() - y)
						* (keys[i].getCenterY() - y);
				if (distance < shortest) {
					shortest = distance;
					symbolKeyID = i;
				}
			}
		}
		return symbolKeyID;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public String getLastKeyboard() {
		return lastKeyboard;
	}

	public void setLastKeyboard(String lastKeyboard) {
		this.lastKeyboard = lastKeyboard;
	}
}
