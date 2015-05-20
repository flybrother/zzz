package com.aeviou.Util;

import com.aeviou.front.keyboardview.PreferenceSetting;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends LauncherActivity {
	
	String[] names = {"设置与帮助", "设为默认输入法", "输入速度"};
	Class<?>[] classes = {PreferenceSetting.class, MainActivity.class, SpeedActivity.class};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
		setListAdapter(adapter);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isVirgin = preferences.getBoolean("isVirgin", true);

		if (isVirgin) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("isVirgin", false);
			editor.commit();
			Intent intent = new Intent(this, HelpActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			this.startActivity(intent);
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = null;
		
		switch (position) {
		case 0:
			intent = new Intent(MainActivity.this, classes[position]);
			this.startActivity(intent);
			break;
		case 1:
			InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
			/*String ss = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
			Log.d("MainActivity", ss);
			List<InputMethodInfo> info = imm.getEnabledInputMethodList();
			for (InputMethodInfo in : info) {
				Log.d("MainActivity", in.getId()+" "+in.getPackageName()+" "+in.getServiceName());
			}
			intent = new Intent();
			intent.setAction("android.settings.INPUT_METHOD_SETTINGS");
			this.startActivity(intent);*/
			imm.showInputMethodPicker();
			break;
		case 2:
			intent = new Intent(MainActivity.this, classes[position]);
			this.startActivity(intent);
			break;
		}
	}
}
