package com.aeviou.front.keyboardview.hexkeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

public class Candidate extends TextView{
	private float xDifference;
	private int startLeft;
	private int startTop;
	
	public Candidate(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
	}
	
	public float getxDifference() {
		return xDifference;
	}

	public void setxDifference(float xDifference) {
		this.xDifference = xDifference;
	}
	
	public int getStartLeft() {
		return startLeft;
	}

	public void setStartLeft(int startLeft) {
		this.startLeft = startLeft;
	}

	public int getStartTop() {
		return startTop;
	}

	public void setStartTop(int startTop) {
		this.startTop = startTop;
	}
}
