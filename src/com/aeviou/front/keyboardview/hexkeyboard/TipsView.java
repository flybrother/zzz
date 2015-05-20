package com.aeviou.front.keyboardview.hexkeyboard;

import com.aeviou.Util.Constants;
import com.aeviou.Util.Globals;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKey;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.PopupWindow;

public class TipsView extends View{

	private PopupWindow mTipWindow;
	private boolean ifShow = true;
	
	private int x, y;
	private int width, height;
	//private Map<Integer, AbstractKey> tipKeys = new HashMap<Integer, AbstractKey>();
	private AbstractKey[] keys;
	
	public TipsView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void onDraw(Canvas canvas) {
		if(Globals.isTipsViewOn && keys != null)
			for (AbstractKey key : keys)
				if (key.getStatus() != Constants.KEY_STATUS_NORMAL)
					key.draw(canvas);
	}
	
	public void update() {
		if (mTipWindow == null) {
			createWindow();
		}
		if (ifShow == false) {
			this.setVisibility(INVISIBLE);
		} else {
			this.setVisibility(VISIBLE);
		}
		if (Globals.view != null) {
			width = Globals.view.getWidth();
			height = Globals.view.getHeight();
			x = 0;
			y = Globals.view.getTop() - height;
			mTipWindow.showAsDropDown(Globals.view);
			mTipWindow.update(x, y, width, height);
			this.invalidate();
		}
	}
	
	public void createWindow() {
		if (mTipWindow != null) {
			closeWindow();
		}
		mTipWindow = new PopupWindow(this);
		mTipWindow.setTouchable(false);
		mTipWindow.setContentView(this);
	}
	
	public void closeWindow() {
		if (mTipWindow == null) {
			return;
		}
		this.setVisibility(INVISIBLE);
		mTipWindow.dismiss();
		mTipWindow = null;
	}

	public AbstractKey[] getKeys() {
		return keys;
	}

	public void setKeys(AbstractKey[] keys) {
		this.keys = keys;
	}

	public boolean isIfShow() {
		return ifShow;
	}

	public void setIfShow(boolean ifShow) {
		this.ifShow = ifShow;
	}
}
