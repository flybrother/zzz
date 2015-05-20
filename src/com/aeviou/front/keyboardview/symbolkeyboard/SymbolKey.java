package com.aeviou.front.keyboardview.symbolkeyboard;

import android.graphics.Canvas;
import com.aeviou.Util.BitmapManager;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKey;

public class SymbolKey extends AbstractKey {
	
	protected String[] values = null;
	protected int curPage = 1;
	
	public SymbolKey(String name, String[] values, int status, String type, int id) {
		this.name = name;
		this.values = values;
		this.id = id;
		this.status = status;
		this.type = type;
	}

	@Override
	public void draw(Canvas cvs) {
		BitmapManager.getInstance().drawSymbolKeyBitmap(leftTopX, leftTopY, name, status, type, "p"+curPage, cvs);
	}

	public String getValue(int num) {
		if (this.values != null) {
			if (num < 0 || num >= this.values.length)
				return "";
			else
				return this.values[num];
		} else {
			return "";
		}
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		if (curPage < 0)
			curPage = 1;
		this.curPage = curPage;
	}
}
