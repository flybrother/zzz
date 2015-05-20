package com.aeviou.front.keyboardview.englishkeyboard;

import android.graphics.Canvas;

import com.aeviou.Util.BitmapManager;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKey;

public class EnglishKey extends AbstractKey{
	
	protected String subtext = "";
	
	public EnglishKey(String name, int status, String type, String subtext, int id){
		this.name = name;
		this.id = id;
		this.status = status;
		this.type = type;
		this.subtext = subtext;
	}
	
	@Override
	public void draw(Canvas Cvs) {
		// TODO Auto-generated method stub
		BitmapManager.getInstance().drawEnglishKeyBitmap(leftTopX, leftTopY, name, status, type, id, Cvs);
	}

	public String getSubtext() {
		return subtext;
	}

	public void setSubtext(String subtext) {
		this.subtext = subtext;
	}

}
