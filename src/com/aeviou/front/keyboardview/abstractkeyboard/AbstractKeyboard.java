package com.aeviou.front.keyboardview.abstractkeyboard;

import android.graphics.Canvas;
import android.graphics.Point;

public abstract class AbstractKeyboard {
	protected AbstractKey[] keys;
	protected Point lastPos;
	
	public static enum keyboardType{
		keyboard_hex, keyboard_en, keyboard_num, keyboard_symbol;
	}
	
	public AbstractKeyboard(){
	}
	
	public abstract void draw(Canvas Cvs);
	
	public abstract boolean onTouch(float x, float y);
	
	public abstract boolean onMove(float x, float y);
	
	public abstract String onRelease(float x, float y);
	
	public AbstractKey[] getKeys(){
		return keys;
	}
	
	public void setKeys(AbstractKey[] Keys){
		this.keys = Keys;
	}
	
	public Point getLastPos(){
		return lastPos;
	}
	
	public void setLastPos(Point LastPos){
		this.lastPos = LastPos;
	}
}
