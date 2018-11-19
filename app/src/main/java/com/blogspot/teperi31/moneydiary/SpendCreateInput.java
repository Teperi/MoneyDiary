package com.blogspot.teperi31.moneydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class SpendCreateInput extends AppCompatActivity {
	
	AppCompatSpinner sp_year;
	AppCompatSpinner sp_month;
	AppCompatSpinner sp_day;
	AppCompatSpinner sp_account;
	AppCompatSpinner sp_category;
	ArrayList<Integer> yearselect;
	ArrayList<Integer> monthselect;
	ArrayList<Integer> dayselect;
	ArrayList<String> accountselect;
	ArrayList<String> categoryselect;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcreatespend);
		
		
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop_basic);
		setActionBar(myToolbar);
		
		// 년도 스피너
		sp_year = findViewById(R.id.inputCreateSpendSubclassDateYearSpinner);
		
		yearselect = new ArrayList<Integer>();
		yearselect.add(2018);
		
		ArrayAdapter yearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yearselect);
		yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_year.setAdapter(yearAdapter);
		
		// 월 스피너
		sp_month = findViewById(R.id.inputCreateSpendSubclassDateMonthSpinner);
		
		monthselect = new ArrayList<Integer>();
		monthselect.add(10);
		monthselect.add(11);
		monthselect.add(12);
		
		ArrayAdapter monthAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, monthselect);
		monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_month.setAdapter(monthAdapter);
		
		// 일 스피너
		sp_day = findViewById(R.id.inputCreateSpendSubclassDateDaySpinner);
		
		dayselect = new ArrayList<Integer>();
		dayselect.add(1);
		dayselect.add(2);
		dayselect.add(3);
		dayselect.add(4);
		dayselect.add(5);
		dayselect.add(6);
		dayselect.add(7);
		dayselect.add(8);
		dayselect.add(9);
		dayselect.add(10);
		dayselect.add(11);
		dayselect.add(12);
		dayselect.add(13);
		dayselect.add(14);
		dayselect.add(15);
		dayselect.add(16);
		dayselect.add(17);
		dayselect.add(18);
		dayselect.add(19);
		dayselect.add(20);
		dayselect.add(21);
		dayselect.add(22);
		dayselect.add(23);
		dayselect.add(24);
		dayselect.add(25);
		dayselect.add(26);
		dayselect.add(27);
		dayselect.add(28);
		dayselect.add(29);
		dayselect.add(30);
		ArrayAdapter dayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dayselect);
		dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_day.setAdapter(dayAdapter);
		
		// 계좌 스피너
		sp_account = findViewById(R.id.inputCreateSpendSubclassAccountSpinner);
		accountselect = new ArrayList<>();
		accountselect.add("현금");
		accountselect.add("체크카드");
		
		ArrayAdapter accountAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, accountselect);
		accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_account.setAdapter(accountAdapter);
		
		// 분류 스피너
		sp_category = findViewById(R.id.inputCreateSpendSubclassCategorySpinner);
		categoryselect = new ArrayList<>();
		categoryselect.add("점심");
		categoryselect.add("저녁");
		categoryselect.add("간식");
		categoryselect.add("회식");
		categoryselect.add("영화");
		
		ArrayAdapter categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryselect);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_category.setAdapter(categoryAdapter);
		
		
		findViewById(R.id.inputCreateSpendComplete).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// 텍스트 가져오기
				EditText inputPriceText = findViewById(R.id.inputCreateSpendSubclassPriceEdit);
				EditText inputUsageText = findViewById(R.id.inputCreateSpendSubclassUsageEdit);
				
				DataMoneyFlow obj = null;
				
				// 금액 확인 후 없으면 toast 띄우기
				if (inputPriceText.getText().toString().trim().length() <= 0) {
					Toast.makeText(SpendCreateInput.this, "금액을 입력하십시오", Toast.LENGTH_SHORT).show();
				} else {
					obj = new DataMoneyFlow("출금",
							Integer.parseInt(sp_year.getSelectedItem().toString()),
							Integer.parseInt(sp_month.getSelectedItem().toString()),
							Integer.parseInt(sp_day.getSelectedItem().toString()),
							sp_account.getSelectedItem().toString(),
							sp_category.getSelectedItem().toString(),
							Integer.parseInt(inputPriceText.getText().toString().trim()),
							inputUsageText.getText().toString());
					Intent i = new Intent(SpendCreateInput.this ,RecyclerviewMoneyFlow.class);
					i.putExtra("InputCreateSpend", obj);
					// 스택 관리
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					startActivity(i);
				}
				
			}
		});
		
		// 취소 버튼 클릭시 팝업 띄움
		// 뒤로가기 버튼에도 같은 기능 구현
		findViewById(R.id.inputCreateSpendCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancelaction = new AlertDialog.Builder(SpendCreateInput.this);
				
				cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				
				cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				
				AlertDialog canclepopup = cancelaction.create();
				canclepopup.setTitle("경고");
				canclepopup.setMessage("지출 기록을 그만 하시겠습니까?");
				canclepopup.show();
				
			}
		});
		
		
		
	}
	// 뒤로가기 버튼 눌렀을 시
	@Override
	public void onBackPressed() {
		
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(SpendCreateInput.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SpendCreateInput.super.onBackPressed();
			}
		});
		
		AlertDialog canclepopup = cancelaction.create();
		canclepopup.setTitle("TEST");
		canclepopup.setMessage("지출 기록을 그만 하시겠습니까?");
		canclepopup.show();
	}
	
	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}
}