package com.aeviou.front;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.aeviou.Util.Client;
import com.aeviou.Util.Constants;
import com.aeviou.Util.Detector;
import com.aeviou.Util.Globals;
import com.aeviou.Util.HelpActivity;
import com.aeviou.Util.SoundManager;
import com.aeviou.Util.XMLReader;
import com.aeviou.front.keyboardview.AeviouKeyboardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

public class AeviouIMEService extends InputMethodService{
	
	private AeviouKeyboardView inputView;
	private Date lastLogDate = null;
	private InputMethodManager imm;

	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("AeviouIMEService","run");
		// initialize
		Globals.service = this;
		XMLReader.getXMLReaderInstance().initialize(this.getResources());
		SoundManager.getInstance().initialize(this.getSystemService(Context.VIBRATOR_SERVICE));
		imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		// load setting
		SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
		Globals.isVibratorOn = shp.getBoolean("vibration", false);
		Globals.isTipsViewOn = shp.getBoolean("tipsview", true);
		Globals.isSpeedViewOn = shp.getBoolean("speedview", false);
	}

	@Override
	public View onCreateInputView() {
		//super.onCreateInputView();
		// inputview
		if (inputView != null){
			inputView.closing();
			inputView = null;
			System.gc();
		}
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isVirgin = preferences.getBoolean("isVirgin", true);
		

		//check dict exist
		File dir = this.getFilesDir();
		File file = new File(dir.getPath() + "/" + "phanzi.jpg");
		if(file.exists()==false){
			try {
				MergeFile("phanzi.jpg", null, "phanzi.jpg");
				MergeFile("pnode.jpg", null, "pnode.jpg");
				MergeFile("proot.jpg", null, "proot.jpg");
				MergeFile("plx.jpg", null, "plx.jpg");
			} catch (Exception ex) {
				/* No enough space */
				
			} 
		}
		
		dir = this.getFilesDir();
		Globals.PINYIN_FILE_DIRECTORY = dir.getPath() + "/";
		
		//inputView = (AeviouKeyboardView)getLayoutInflater().inflate(R.layout.popup, null);
		inputView = (AeviouKeyboardView)View.inflate(this, R.layout.input, null);
		//inputView = new AeviouKeyboardView(this);
		
		if (isVirgin) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("isVirgin", false);
			editor.commit();
			Intent intent = new Intent(this, HelpActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			this.startActivity(intent);
		}
		
		return inputView;
	}

	// need a thread to do net related work
	// because it's forbidden in main thread
	Runnable logThread = new Runnable() {
		public void run() {
			// check date
			Date date = new Date();
			if (lastLogDate != null && date.getDate() == lastLogDate.getDate()) {
				return;
			}
			// check network
			if (!Detector.hasNetwork(getApplicationContext())) {
				return;
			}
			// send log
			String deviceId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
			String model = Detector.isTablet(getApplicationContext()) ? "tablet" : "phone";
			try {
				URL url = new URL(Constants.addUserUrl+"?id="+deviceId+"&model="+model);
				HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
				urlConn.connect();
				
				InputStreamReader in = new InputStreamReader(urlConn.getInputStream());    
                BufferedReader buffer = new BufferedReader(in);  
                String inputLine = null;  
                while (((inputLine = buffer.readLine()) != null))  
                {  
                	Log.d("yzy", inputLine);  
                }  
                in.close();  
				
				urlConn.disconnect();
			} catch (Exception e) {
			}
			// update lastLogDate
			lastLogDate = date;
		}
	};
	@Override
	// connect server and send userlog
	public void onWindowShown() {
		Date date = new Date();
		if (lastLogDate != null && date.getDate() == lastLogDate.getDate()) {
			return;
		}
		if (!Detector.hasNetwork(getApplicationContext())) {
			return;
		}
		String deviceId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
		String model = Detector.isTablet(getApplicationContext()) ? "tablet" : "phone";
		Client.getInstance().addUserlog(deviceId, model);		
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		// TODO Auto-generated method stub
		super.onStartInputView(info, restarting);
		if(info.inputType == info.TYPE_CLASS_DATETIME){
			Log.d("AeviouIMEService","0");
			inputView.switchToSymbolKeyboard();
		}else if(info.inputType == info.TYPE_CLASS_NUMBER){
			Log.d("AeviouIMEService","1");
			inputView.switchToSymbolKeyboard();
		}else if(info.inputType == info.TYPE_TEXT_VARIATION_URI || info.inputType == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS || info.inputType == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_SUBJECT){
			Log.d("AeviouIMEService","2");
			inputView.switchToEnglishKeyboard();
		}else if(info.inputType == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD || info.inputType == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD || info.inputType == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD){
			Log.d("AeviouIMEService","3");
			inputView.switchToEnglishKeyboard();
		}else if(info.inputType == EditorInfo.TYPE_TEXT_VARIATION_NORMAL){
			Log.d("AeviouIMEService","4");
			inputView.switchToHexKeyboard();
		}else if(info.inputType == EditorInfo.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT){
			Log.d("AeviouIMEService","5");
			inputView.switchToHexKeyboard();
		}else if(info.inputType == EditorInfo.TYPE_CLASS_TEXT){
			Log.d("AeviouIMEService","6");
			inputView.switchToHexKeyboard();
		}else{
			Log.d("AeviouIMEService","type:" + info.inputType);
			inputView.switchToHexKeyboard();
		}
	}

	@Override
	public void onFinishInputView(boolean finishingInput) {
		super.onFinishInputView(finishingInput);
		if(inputView != null){
			inputView.onFinishInputView();
			inputView.invalidate();
		}
	}
	
	@Override
	public void onFinishInput() {
		super.onFinishInput();
	}

	public void MergeFile(String file1,String file2, String file3) throws IOException{
		InputStream inputStream1 = this.getResources().getAssets().open(file1, AssetManager.ACCESS_BUFFER);
		InputStream inputStream2 = null;
		if (file2 != null)
			inputStream2 = this.getResources().getAssets().open(file2, AssetManager.ACCESS_BUFFER);
		File dir = this.getFilesDir();
		File file = new File(dir.getPath() + "/" + file3);
		if(file.exists())
			file.delete();
		if(!file.createNewFile()){
			return;
		}
		int count = 0;
		FileOutputStream outputStream = new FileOutputStream(file);
		byte[] buffer = new byte[8 * 1024];
		while((count = inputStream1.read(buffer)) > 0){
			outputStream.write(buffer, 0, count) ;
		}
		inputStream1.close();
		if (file2 != null){
			while((count = inputStream2.read(buffer)) > 0){
				outputStream.write(buffer, 0, count) ;
			}
			inputStream2.close();
		}
		outputStream.close();
	}
}
