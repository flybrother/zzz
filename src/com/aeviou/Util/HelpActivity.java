package com.aeviou.Util;

import com.aeviou.front.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class HelpActivity extends Activity{
	
	private View help_1,help_2,help_3,help_4;
	private int index;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		help_1 = (View)View.inflate(this, R.layout.help_1, null);
		index = 1;
		setContentView(help_1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(index == 1){
				index++;
				help_2 = (View)View.inflate(this, R.layout.help_2, null);
				setContentView(help_2);
				help_1 = null;
			}else if(index == 2){
				index++;
				help_3 = (View)View.inflate(this, R.layout.help_3, null);
				setContentView(help_3);
				help_2 = null;
			}else if(help_4 == null){
				index++;
				help_4 = (View)View.inflate(this, R.layout.help_4, null);
				setContentView(help_4);
				help_3 = null;
			}else{
				help_4 = null;
				this.finish();
			}
		}
		return super.onTouchEvent(event);
	}
	
}
