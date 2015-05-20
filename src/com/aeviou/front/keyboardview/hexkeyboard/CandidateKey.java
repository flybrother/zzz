package com.aeviou.front.keyboardview.hexkeyboard;

import com.aeviou.Util.BitmapManager;
import com.aeviou.Util.Constants;

import android.graphics.Canvas;

public class CandidateKey extends HexKey{
	private String word;

	public CandidateKey(){
		this.name = "candidate";
		this.type = "candidate";
		this.status = Constants.KEY_STATUS_HIDE;
		this.word = "";
	}
	
	@Override
	public void draw(Canvas Cvs) {
		if(this.status != Constants.KEY_STATUS_HIDE)
			BitmapManager.getInstance().drawHexKeyBitmap(leftTopX, leftTopY, name, status, Cvs);
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
