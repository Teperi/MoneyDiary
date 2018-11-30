package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/*
* 처음 시작할 때 Splash 를 띄워주는 class
*
* 메니페스트에 theme 을 지정해서 사용
* android:theme="@style/SplashTheme" 사용
*
* 이 화면 이후 로그인 창 띄우기
* */

public class SplashActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent(this, SignInAppStartActivity.class);
		startActivity(intent);
		
		finish();
	}
}
