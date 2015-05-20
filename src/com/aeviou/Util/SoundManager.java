package com.aeviou.Util;

import android.os.Vibrator;

public class SoundManager {
	private static SoundManager instance;
	private static Vibrator vibrator;
	private static boolean initialized = false;
	
	private SoundManager(){
	}
	
	public void initialize(Object systemservice){
		vibrator = (Vibrator)systemservice;
		initialized = true;
	}
	
	public static SoundManager getInstance(){
		return instance = (instance == null ? new SoundManager() : instance);
	}
	
	public void vibrate(){
		if(initialized){
			try {
				vibrator.vibrate(20);
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		
	}
}
