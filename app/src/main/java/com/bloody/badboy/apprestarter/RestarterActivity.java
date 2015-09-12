package com.bloody.badboy.apprestarter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class RestarterActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 2000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restarter_activity_main);

		new Handler().postDelayed(new Runnable(){

				public void run() {
					Intent mainIntent = new Intent(RestarterActivity.this,a.class);
					RestarterActivity.this.startActivity(mainIntent);
					RestarterActivity.this.finish();
				}

			}, SPLASH_DISPLAY_LENGHT);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
