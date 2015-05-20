package com.aeviou.front.keyboardview;

import com.aeviou.Util.Globals;
import com.aeviou.front.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.CheckBoxPreference;  
import android.util.Log;

public class PreferenceSetting extends PreferenceActivity{
	private CheckBoxPreference vibratePreference;
	private CheckBoxPreference tipsViewPreference;
	private CheckBoxPreference speedViewPreference;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.preference);
		vibratePreference = (CheckBoxPreference)findPreference("vibration");
		tipsViewPreference = (CheckBoxPreference)findPreference("tipsview");
		speedViewPreference = (CheckBoxPreference)findPreference("speedview");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if(preference == vibratePreference){
			if(vibratePreference.isChecked()){
				Globals.isVibratorOn = true;
				Log.d("preference","vibrator true");
			}else{
				Globals.isVibratorOn = false;
				Log.d("preference","vibrator false");
			}
        }else if(preference == tipsViewPreference){
        	if(tipsViewPreference.isChecked()){
        		Globals.isTipsViewOn = true;
        		Log.d("preference","tip view true");
        	}else{
        		Globals.isTipsViewOn = false;
        		Log.d("preference","tip view false");
        	}
        }else if(preference == speedViewPreference){
        	if(speedViewPreference.isChecked()){
        		Globals.isSpeedViewOn = true;
        		Globals.ifReDrawSpeedView = true;
        		Log.d("preference","speed view true");
        	}else{
        		Globals.isSpeedViewOn = false;
        		Globals.ifReDrawSpeedView = false;
        		Log.d("preference","speed view false");
        	}
        }
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}
