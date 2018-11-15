package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class test extends AppCompatActivity {
	
	List<MoneyDiaryList> mdList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		
		mdList = new ArrayList<>();
		
		mdList.add(new MoneyDiaryList("0", "입금" ,2018, 10, 25, "현금", "월급", 200000, "월급"));
		mdList.add(new MoneyDiaryList("1", "이체" ,2018, 10, 25, "체크카드", "이체", 150000, "월급이체"));
		mdList.add(new MoneyDiaryList("2", "출금" ,2018, 10, 26, "체크카드", "점심", 6000, "백채김치찌개"));
		mdList.add(new MoneyDiaryList("3", "출금" ,2018, 10, 26, "체크카드", "회식", 10000, "한신포차"));
		mdList.add(new MoneyDiaryList("4", "출금" ,2018, 10, 27, "현금", "점심", 5000, "할매순대국"));
		mdList.add(new MoneyDiaryList("5", "출금" ,2018, 11, 1, "체크카드", "회식", 10000, "국물떡볶이"));
		mdList.add(new MoneyDiaryList("6", "출금" ,2018, 11, 1, "현금", "저녁", 7000, "피자스쿨"));
		mdList.add(new MoneyDiaryList("7", "출금" ,2018, 11, 2, "현금", "간식", 7000, "GS25"));
		mdList.add(new MoneyDiaryList("8", "출금" ,2018, 11, 2, "체크카드", "영화", 10000, "CGV"));
		
		
		
		
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
