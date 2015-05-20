package com.aeviou.front.keyboardview.hexkeyboard;

import com.aeviou.Util.Globals;
import com.aeviou.Util.WordsStatistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

public class SpeedView extends View {

	private PopupWindow mTipWindow;
	private boolean ifShow = true;
	
	private int x, y;
	private int width, height;
	private Paint painter = null;
	
	public SpeedView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		painter = new Paint();
		painter.setColor(Color.GREEN);
		painter.setStrokeWidth(5);
	}
	
	public void onDraw(Canvas canvas) {
		int showLength = 0;
		double speed = WordsStatistics.getCurrentSpeed();
		double upperLimit = WordsStatistics.getUpperLimit();
		if (speed > 0.0001) {
			if (speed > upperLimit) {
				showLength = Globals.screenwidth;
			} else {
				showLength = (int)((speed/upperLimit) * Globals.screenwidth);
			}
		}
		canvas.drawLine(0, 0, showLength, 0, painter);
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
			height = 5;
			x = 0;
			y = Globals.view.getTop();
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
	
	public boolean isIfShow() {
		return ifShow;
	}

	public void setIfShow(boolean ifShow) {
		this.ifShow = ifShow;
	}
}
