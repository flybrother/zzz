package com.aeviou.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SpeedActivity extends ListActivity {
	String[] titles = {"最快输入速度", "当前排名"};
	String[] preferencesNames = {"maxSpeed", "currentRank"};
	String[] descriptions = {"尚未获得数据", "尚未参与排名"};
	String[] prefix = {"", "第 "};
	String[] postfix = {" 字/分钟", " 名"};
	List<Map<String, String>> listData = null;
	SimpleAdapter adapter = null;

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView listView = new ListView(this);
		listData = getData();
		adapter = new SimpleAdapter(this, 
				listData, 
				android.R.layout.simple_list_item_2,
				new String[] {"title", "description"},
                new int[] {android.R.id.text1, android.R.id.text2});
		listView.setId(android.R.id.list);
		listView.setAdapter(adapter);
		this.setContentView(listView);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String str = "";
		String rank = "";
		int speed = -1;
		
		switch (position) {
		case 0: // current speed
			speed = (int) WordsStatistics.getMaxSpeed();
			Log.d("SpeedActivity", "speed " + speed);
			if (speed >= 0) {
				str = preferences.getString(preferencesNames[0], "");
				if (str.isEmpty() || speed > Integer.parseInt(str)) {
					str = "" + speed;
					Editor editor = preferences.edit();
					editor.putString(preferencesNames[0], str);
					editor.commit();
				}
			}
			Log.d("SpeedActivity", "preferences speed " + str);
			if (!str.isEmpty()) {
				listData.get(0).put("description", prefix[0]+str+postfix[0]);
				adapter.notifyDataSetChanged();
			}
			break;
		case 1: // current rank
			str = preferences.getString(preferencesNames[0], "");
			if (!str.isEmpty()) {
				String deviceId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
				Log.d("SpeedActivity", "deviece id " + deviceId);
				String contact = "";
				speed = Integer.parseInt(str);
				Client.getInstance().updateSpeed(deviceId, speed, contact);
				rank = Client.getInstance().getRank(deviceId);
			}
			if (!rank.isEmpty()) {
				Editor editor = preferences.edit();
				editor.putString(preferencesNames[1], rank);
				editor.commit();
				listData.get(1).put("description", prefix[1]+rank+postfix[1]);
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}
	
	private List<Map<String, String>> getData() {
		List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		for (int i = 0; i < titles.length; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("title", titles[i]);
			String str = preferences.getString(preferencesNames[i], "");
			Log.d("SpeedActivity", str);
			if (str.isEmpty()) {
				map.put("description", descriptions[i]);
			} else {
				map.put("description", prefix[i]+str+postfix[i]);
			}
			listData.add(map);
		}
		
		return listData;
	}
}
