package com.aeviou.front.keyboardview;

import com.aeviou.front.R;
import com.aeviou.Util.BitmapManager;
import com.aeviou.Util.Globals;
import com.aeviou.Util.SoundManager;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKeyboard;
import com.aeviou.front.keyboardview.englishkeyboard.EnglishKeyboard;
import com.aeviou.front.keyboardview.englishkeyboard.EnglishKeyboardFactory;
import com.aeviou.front.keyboardview.hexkeyboard.HexKeyboard;
import com.aeviou.front.keyboardview.hexkeyboard.HexKeyboardFactory;
import com.aeviou.front.keyboardview.symbolkeyboard.SymbolKeyboard;
import com.aeviou.front.keyboardview.symbolkeyboard.SymbolKeyboardFactory;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * @author user
 *
 */
public class AeviouKeyboardView extends KeyboardView implements ValueAnimator.AnimatorUpdateListener{
	
	
	private AbstractKeyboard currentKeyboard = null;
	private Canvas cvs;
	private Bitmap background;
	private PaintFlagsDrawFilter drawFilter;
	private DisplayMetrics displaymetrics;
	private int width,height;
	private boolean isInitialized = false;
	private static HexKeyboard hexKeyboard;
	private static EnglishKeyboard englishKeyboard;
	private static SymbolKeyboard symbolKeyboard;
	private static int lastOrientation;
	private static float hexkeyboardOffset = 0;
		
	/**
	 * @param context
	 * @param attrs
	 */
	public AeviouKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//initialize displaymetrics
		Globals.view = this;
		int orien = Globals.service.getResources().getConfiguration().orientation;
		displaymetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displaymetrics);
		Globals.screenwidth = displaymetrics.widthPixels;
		Globals.screenheight = displaymetrics.heightPixels;
		if(orien == Globals.service.getResources().getConfiguration().ORIENTATION_PORTRAIT){
			Log.d("AeviouKeyboardView","----------------------------------");
			Log.d("AeviouKeyboardView","portrait!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Log.d("AeviouKeyboardView", "screen width "+Globals.screenwidth+" / screen height "+Globals.screenheight);
			if(hexKeyboard == null){//default keyboard(hexkeyboard) is null: first time running
				Log.d("AeviouKeyboardView","firstTime");
				if (Globals.screenwidth > Globals.screenheight) {
					HexKeyboardFactory.getInstance().initialize(displaymetrics.heightPixels, displaymetrics.widthPixels);
					hexKeyboard = HexKeyboardFactory.getInstance().createKeyboard();
					BitmapManager.getInstance().initializeHexKeyBitmap(HexKeyboardFactory.getInstance().getHexkeyWidth(), 
							HexKeyboardFactory.getInstance().getHexkeyHeight(), 
							displaymetrics.heightPixels, displaymetrics.widthPixels, this.getResources());
				} else {
					HexKeyboardFactory.getInstance().initialize(displaymetrics.widthPixels, displaymetrics.heightPixels);
					hexKeyboard = HexKeyboardFactory.getInstance().createKeyboard();
					BitmapManager.getInstance().initializeHexKeyBitmap(HexKeyboardFactory.getInstance().getHexkeyWidth(), 
							HexKeyboardFactory.getInstance().getHexkeyHeight(), 
							displaymetrics.widthPixels, displaymetrics.heightPixels, this.getResources());
				}
				currentKeyboard = hexKeyboard;
				keyboardInit.run();
			}
		}else if(orien == Globals.service.getResources().getConfiguration().ORIENTATION_LANDSCAPE){
			Log.d("AeviouKeyboardView","----------------------------------");
			Log.d("AeviouKeyboardView","landscape!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		
		
		/*
		if(lastOrientation == 0){//first time
			Log.d("AeviouKeyboardView","lastOrientation:" + lastOrientation);//test!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			lastOrientation = Globals.service.getResources().getConfiguration().orientation;
			if(lastOrientation == Globals.service.getResources().getConfiguration().ORIENTATION_PORTRAIT){//orientation portrait
				Log.d("AeviouKeyboardView","orientation portrait " + lastOrientation);
				if(hexKeyboard == null){//set default keyboard - hexkeyboard
					HexKeyboardFactory.getInstance().initialize(displaymetrics.widthPixels, displaymetrics.heightPixels);
					hexKeyboard = HexKeyboardFactory.getInstance().createKeyboard();
					BitmapManager.getInstance().initializeHexKeyBitmap(HexKeyboardFactory.getInstance().getHexkeyWidth(), HexKeyboardFactory.getInstance().getHexkeyHeight(), displaymetrics.widthPixels, displaymetrics.heightPixels, this.getResources());
				}
				currentKeyboard = hexKeyboard;
			}else if(lastOrientation == Globals.service.getResources().getConfiguration().ORIENTATION_LANDSCAPE){//orientation landscape
				Log.d("AeviouKeyboardView","orientation landscape " + lastOrientation);
			}
		}else if(Globals.service.getResources().getConfiguration().orientation != lastOrientation){//screen orientation changed
			Log.d("AeviouKeyboardView","orientation changed");
			lastOrientation = Globals.service.getResources().getConfiguration().orientation;
			if(currentKeyboard instanceof HexKeyboard){
				Log.d("AeviouKeyboardView","HexKeyboard orientation changed");
				BitmapManager.getInstance().deleteHexKeyBitmap();
			}else if(currentKeyboard instanceof EnglishKeyboard){
				Log.d("AeviouKeyboardView","EnglishKeyboard orientation changed");
				BitmapManager.getInstance().deleteEnglishKeyBitmap();
				EnglishKeyboardFactory.getInstance().initialize(displaymetrics.widthPixels, displaymetrics.heightPixels);
				englishKeyboard = EnglishKeyboardFactory.getInstance().createKeyboard();
				BitmapManager.getInstance().initializeEnglishKeyBitmap(EnglishKeyboardFactory.getInstance().getEnglishkeyWidth(), EnglishKeyboardFactory.getInstance().getEnglishkeyHeight(), EnglishKeyboardFactory.getInstance().getPadding(), this.getResources());
				currentKeyboard = englishKeyboard;
			}else if(currentKeyboard instanceof SymbolKeyboard){
				Log.d("AeviouKeyboardView","SymbolKeyboard orientation changed");
				BitmapManager.getInstance().deleteSymbolKeyBitmap();
			}else{
				//error
				Log.e("AeviouKeyboardView", "no current keyboard");
			}
		}else{//screen orientation not changed yet
			
		}
		*/
		//set default draw filter
		//drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	}
	
	private Runnable keyboardInit = new Runnable(){
		public void run(){
			if(englishKeyboard == null){
				if (displaymetrics.widthPixels > displaymetrics.heightPixels) {
					EnglishKeyboardFactory.getInstance().initialize(displaymetrics.heightPixels, displaymetrics.widthPixels);
				} else {
					EnglishKeyboardFactory.getInstance().initialize(displaymetrics.widthPixels, displaymetrics.heightPixels);
				}
				englishKeyboard = EnglishKeyboardFactory.getInstance().createKeyboard();
				BitmapManager.getInstance().initializeEnglishKeyBitmap(EnglishKeyboardFactory.getInstance().getEnglishkeyWidth(), 
						EnglishKeyboardFactory.getInstance().getEnglishkeyHeight(), 
						EnglishKeyboardFactory.getInstance().getPadding(), getResources());
				Log.d("AeviouKeyboardView","englishKeyboard initialized");
			}
			if(symbolKeyboard == null){
				if (displaymetrics.widthPixels > displaymetrics.heightPixels) {
					SymbolKeyboardFactory.getInstance().initialize(displaymetrics.heightPixels, displaymetrics.widthPixels);
				} else {
					SymbolKeyboardFactory.getInstance().initialize(displaymetrics.widthPixels, displaymetrics.heightPixels);
				}
				symbolKeyboard = SymbolKeyboardFactory.getInstance().createKeyboard();
				BitmapManager.getInstance().initializeSymbolKeyBitmap(SymbolKeyboardFactory.getInstance().getSymbolkeyWidth(), SymbolKeyboardFactory.getInstance().getSymbolkeyHeight(), SymbolKeyboardFactory.getInstance().getPadding(), getResources());
				Log.d("AeviouKeyboardView","symbolKeyboard initialized");
			}
		}
	};
	
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == newConfig.ORIENTATION_LANDSCAPE){
			this.setVisibility(View.GONE);
		}else if(newConfig.orientation == newConfig.ORIENTATION_LANDSCAPE){
			this.setVisibility(View.VISIBLE);
			this.setEnabled(true);
		}
		/*
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == newConfig.ORIENTATION_PORTRAIT){
			Log.d("AeviouKeyboardView","portrait");
			if(currentKeyboard instanceof HexKeyboard){
				if(hexkeyboardOffset == 0){
					hexkeyboardOffset = (displaymetrics.widthPixels - displaymetrics.heightPixels)/2;
				}else{
					hexkeyboardOffset = 0 - hexkeyboardOffset;
				}
				((HexKeyboard) currentKeyboard).setHexKeyOffset(hexkeyboardOffset);
			}else if(currentKeyboard instanceof EnglishKeyboard){
				
			}else if(currentKeyboard instanceof SymbolKeyboard){
				
			}else{
				Log.e("AeviouKeyboardView","currentKeyboard is null");
			}
		}else if(newConfig.orientation == newConfig.ORIENTATION_LANDSCAPE){
			Log.d("AeviouKeyboardView","landscape");
			if(currentKeyboard instanceof HexKeyboard){
				if(hexkeyboardOffset == 0){
					hexkeyboardOffset = (displaymetrics.widthPixels - displaymetrics.heightPixels)/2;
				}else{
					hexkeyboardOffset = 0 - hexkeyboardOffset;
				}
				((HexKeyboard) currentKeyboard).setHexKeyOffset(hexkeyboardOffset);
			}else if(currentKeyboard instanceof EnglishKeyboard){
				
			}else if(currentKeyboard instanceof SymbolKeyboard){
				
			}else{
				Log.e("AeviouKeyboardView","currentKeyboard is null");
			}
		}
		*/
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(this.displaymetrics);
		width = displaymetrics.widthPixels;
		height = (int)(HexKeyboardFactory.getInstance().getPadding()*5 + HexKeyboardFactory.getInstance().getHexkeyHeight()*4.25);
		if (background == null){
			Matrix matrix = new Matrix();
			Bitmap temp = BitmapFactory.decodeResource(this.getResources(), R.drawable.background).copy(Bitmap.Config.ARGB_8888, true);
			matrix.postScale((float)width/temp.getWidth(), (float)height/temp.getHeight());
			this.setBackgroundResource(R.drawable.background);
		}
		this.setMeasuredDimension(width, height);
		Log.d("AeviouKeyboardView", "onMesuare width: "+width+" height: "+height);
		isInitialized = true;
		Globals.ifReDrawSpeedView = true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if(isInitialized){
			currentKeyboard.draw(canvas);
			
			if (currentKeyboard instanceof HexKeyboard && Globals.ifReDrawSpeedView && Globals.isSpeedViewOn) {
				Globals.ifReDrawSpeedView = false;
				hexKeyboard.startShowSpeed();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		// TODO Auto-generated method stub
		if(!isInitialized){
			return true;
		}
		boolean needRedrawn = true;
		switch(me.getAction()){
		case MotionEvent.ACTION_DOWN:
			needRedrawn = currentKeyboard.onTouch(me.getX(), me.getY());
			if(Globals.isVibratorOn)
				SoundManager.getInstance().vibrate();
			break;
		case MotionEvent.ACTION_MOVE:
			needRedrawn = currentKeyboard.onMove(me.getX(), me.getY());
			break;
		case MotionEvent.ACTION_UP:
			String keyboardEvent = currentKeyboard.onRelease(me.getX(), me.getY());
			if(keyboardEvent != null) {
				if (keyboardEvent.equals("en")) {
					if(englishKeyboard == null){
						
					}else{
						currentKeyboard = englishKeyboard;
					}
				} else if (keyboardEvent.equals("ch")) {
					if(hexKeyboard == null){
						if (displaymetrics.widthPixels > displaymetrics.heightPixels) {
							HexKeyboardFactory.getInstance().initialize(displaymetrics.heightPixels, displaymetrics.widthPixels);
							hexKeyboard = HexKeyboardFactory.getInstance().createKeyboard();
							BitmapManager.getInstance().initializeHexKeyBitmap(HexKeyboardFactory.getInstance().getHexkeyWidth(), 
									HexKeyboardFactory.getInstance().getHexkeyHeight(), 
									displaymetrics.heightPixels, displaymetrics.widthPixels, this.getResources());
						} else {
							HexKeyboardFactory.getInstance().initialize(displaymetrics.widthPixels, displaymetrics.heightPixels);
							hexKeyboard = HexKeyboardFactory.getInstance().createKeyboard();
							BitmapManager.getInstance().initializeHexKeyBitmap(HexKeyboardFactory.getInstance().getHexkeyWidth(), 
									HexKeyboardFactory.getInstance().getHexkeyHeight(), 
									displaymetrics.widthPixels, displaymetrics.heightPixels, this.getResources());
						}
					}
					currentKeyboard = hexKeyboard;
					hexKeyboard.resumeShowSpeed();
				} else if (keyboardEvent.equals("symbols")) {
					if(symbolKeyboard == null){
					}
					else{
						if (currentKeyboard instanceof HexKeyboard)
							symbolKeyboard.setLastKeyboard("ch");
						else if (currentKeyboard instanceof EnglishKeyboard)
							symbolKeyboard.setLastKeyboard("en");
						currentKeyboard = symbolKeyboard;
					}
				} else if (keyboardEvent.equals("setting")) {
					Intent intent = new Intent(Globals.service, PreferenceSetting.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Globals.service.startActivity(intent);
				} 
			}
			//resetView();
		}
		if(needRedrawn){
			this.invalidate();
		}
		return true;
	}
	
	public void onFinishInputView(){
		if(hexKeyboard != null){
			hexKeyboard.onResetHexKeyboard();
			//resetView();
		}
		//currentKeyboard = hexKeyboard;
	}
	
	public void onPreferenceSettingChanged() {
		if (currentKeyboard instanceof HexKeyboard && Globals.isSpeedViewOn) {
			hexKeyboard.startShowSpeed();
		}
	}
	
	private void resetView(){
		/*
		background = background_copy.copy(Bitmap.Config.ARGB_8888, true);
		cvs = new Canvas(background);
		cvs.setDrawFilter(drawFilter);
		*/
	}

	@Override
	public void onAnimationUpdate(ValueAnimator arg0) {
		// TODO Auto-generated method stub
		invalidate();
	}
	
	public void switchToHexKeyboard(){
		if(hexKeyboard == null){
			HexKeyboardFactory.getInstance().initialize(displaymetrics.widthPixels, displaymetrics.heightPixels);
			hexKeyboard = HexKeyboardFactory.getInstance().createKeyboard();
			BitmapManager.getInstance().initializeHexKeyBitmap(HexKeyboardFactory.getInstance().getHexkeyWidth(), HexKeyboardFactory.getInstance().getHexkeyHeight(), displaymetrics.widthPixels, displaymetrics.heightPixels, this.getResources());
		}
		currentKeyboard = hexKeyboard;
	}
	
	public void switchToEnglishKeyboard(){
		if(englishKeyboard == null){
			EnglishKeyboardFactory.getInstance().initialize(displaymetrics.widthPixels, displaymetrics.heightPixels);
			englishKeyboard = EnglishKeyboardFactory.getInstance().createKeyboard();
			BitmapManager.getInstance().initializeEnglishKeyBitmap(EnglishKeyboardFactory.getInstance().getEnglishkeyWidth(), EnglishKeyboardFactory.getInstance().getEnglishkeyHeight(), EnglishKeyboardFactory.getInstance().getPadding(), this.getResources());
		}
		currentKeyboard = englishKeyboard;
	}
	
	public void switchToSymbolKeyboard(){
		if(symbolKeyboard == null){
			SymbolKeyboardFactory.getInstance().initialize(displaymetrics.widthPixels, displaymetrics.heightPixels);
			symbolKeyboard = SymbolKeyboardFactory.getInstance().createKeyboard();
			BitmapManager.getInstance().initializeSymbolKeyBitmap(SymbolKeyboardFactory.getInstance().getSymbolkeyWidth(), SymbolKeyboardFactory.getInstance().getSymbolkeyHeight(), SymbolKeyboardFactory.getInstance().getPadding(), this.getResources());
		}
		if (currentKeyboard instanceof HexKeyboard)
			symbolKeyboard.setLastKeyboard("ch");
		else if (currentKeyboard instanceof EnglishKeyboard)
			symbolKeyboard.setLastKeyboard("en");
		currentKeyboard = symbolKeyboard;
	}
}
