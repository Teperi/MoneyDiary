package com.blogspot.teperi31.moneydiary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class AppInfo extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appinfo);
		
		Toolbar mToolbar = findViewById(R.id.activity_appinfo_toolbarTop);
		mToolbar.setTitle("앱 소개");
		setSupportActionBar(mToolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()){
			overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
		}
		
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return super.onSupportNavigateUp();
	}
}
