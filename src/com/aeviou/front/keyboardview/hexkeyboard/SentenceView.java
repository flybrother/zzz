package com.aeviou.front.keyboardview.hexkeyboard;

import com.aeviou.Util.Globals;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

public class SentenceView extends View {
	public static final int GAP = 10;
	
	private PopupWindow mSentenceWindow;
	private SentenceView sentenceView;
	private Handler handler;
	private int textSize;
	private Paint paint;
	private PaintFlagsDrawFilter drawFilter;
	private String sentence;
	private int width, height;

	public SentenceView(Context context, int textsize) {
		super(context);
		textSize = textsize;
		drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(1);
		setBackgroundColor(Color.argb(200, 255, 255, 255));
		mSentenceWindow = null;
		sentenceView = this;
		handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					sentenceView.update();
					break;
				}
				super.handleMessage(msg);
			}
		};
	}
	
	public Handler getHandler(){
		return handler;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if (mSentenceWindow == null) 
			return;
		canvas.setDrawFilter(drawFilter);
		if (sentence != null){
			canvas.drawText(sentence, 2 , textSize, paint);
		}
		canvas.drawLine(0, 0, width, 0, paint);
		canvas.drawLine(0, height, width, height, paint);
		canvas.drawLine(width, 1, width, height, paint);
		canvas.drawLine(0, 0, 0, height, paint);
	}

	public void setSentence(String sentence){
		this.sentence = sentence;
		if (sentence == null || sentence.equals(" ")){
			width = 0;
		}else{
			paint.setTextSize(textSize);
			width = (int)paint.measureText(sentence) + 5;
		}
	}
	
	public void commitUpdate(){
		Message msg= new Message();
		msg.what = 1;
		handler.sendMessage(msg);
	}
	
	public void closeWindow(){
		if (mSentenceWindow == null) return;
		this.setVisibility(INVISIBLE);
		mSentenceWindow.dismiss();
		mSentenceWindow = null;
	}
	
	public void reset(){
		createWindow();
	}
	
	private void update(){
		if (mSentenceWindow == null){
			createWindow();
		}
		if (width == 0){
			this.setVisibility(INVISIBLE);
		}else{
			this.setVisibility(VISIBLE);
		}
		
		height = (int)(textSize*1.2);
		mSentenceWindow.showAsDropDown(Globals.view);
		mSentenceWindow.update(0, -height, width, height);
		this.invalidate();
	}
	
	private void createWindow(){
		if (mSentenceWindow != null){
			closeWindow();
		}
		mSentenceWindow = new PopupWindow(this);
		mSentenceWindow.setTouchable(false);
		mSentenceWindow.setContentView(this);
	}
}


