package com.aeviou.Util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class OpenUrlActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://aevioutest.sinaapp.com/index.php"));
		startActivity(intent);
	}
	
	protected void onStop() {
		super.onStop();
		this.finish();
	}
}
