package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class test extends AppCompatActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		
		// 데이터를 받기 위한 인텐트 생성
		Intent intentDate = getIntent();
		String inputDate = intentDate.getStringExtra("saveDate");
		
		
		
		
		// 데이터를 텍스트 자리에 입력
		TextView dateInputToString = findViewById(R.id.inputdate);
		dateInputToString.setText(inputDate);
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
	}
}
