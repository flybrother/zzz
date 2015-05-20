package com.aeviou.front.keyboardview.hexkeyboard;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKey;

public class CandidateWord extends AbstractKey{
	private Paint paint;
	private float startX;
	private float startY;
	private float xDifference;

	@Override
	public void draw(Canvas Cvs) {
		// TODO Auto-generated method stub
		Cvs.drawText(this.getName(), this.getCenterX()-(float)paint.getTextSize()/2, this.getCenterY()+(float)paint.getTextSize()/3, paint);
	}
	
	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	public float getStartX() {
		return startX;
	}

	public void setStartX(float startX) {
		this.startX = startX;
	}

	public float getStartY() {
		return startY;
	}

	public void setStartY(float startY) {
		this.startY = startY;
	}
	
	public float getxDifference() {
		return xDifference;
	}

	public void setxDifference(float xDifference) {
		this.xDifference = xDifference;
	}
}
