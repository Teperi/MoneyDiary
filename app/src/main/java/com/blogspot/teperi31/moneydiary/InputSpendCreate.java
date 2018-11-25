package com.blogspot.teperi31.moneydiary;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class InputSpendCreate extends AppCompatActivity {
	
	AppCompatSpinner sp_account;
	AppCompatSpinner sp_category;
	ArrayList<String> accountselect;
	ArrayList<String> categoryselect;
	
	Calendar myCalendar;
	TextView datepicker;
	DatePickerDialog.OnDateSetListener date;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcreatespend);
		
		
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop_basic);
		setActionBar(myToolbar);
		
		myCalendar = Calendar.getInstance(Locale.KOREA);
		
		datepicker = findViewById(R.id.inputCreateSpendSubclassDateEdit);
		
		updateLabel();
		
		date = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
				myCalendar.set(year, month, dayOfMonth);
				updateLabel();
			}
		};
		
		datepicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(InputSpendCreate.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		
		
		
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
					Toast.makeText(InputSpendCreate.this, "금액을 입력하십시오", Toast.LENGTH_SHORT).show();
				} else {
					obj = new DataMoneyFlow("출금",
							myCalendar.getTime(),
							sp_account.getSelectedItem().toString(),
							sp_category.getSelectedItem().toString(),
							Integer.parseInt(inputPriceText.getText().toString().trim()),
							inputUsageText.getText().toString(),
							ApplicationClass.mfList.size()+1);
					Intent i = new Intent(InputSpendCreate.this ,RecyclerviewMoneyFlow.class);
					ApplicationClass.mfList.add(obj);
//					// 스택 관리
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
				
			}
		});
		
		// 취소 버튼 클릭시 팝업 띄움
		// 뒤로가기 버튼에도 같은 기능 구현
		findViewById(R.id.inputCreateSpendCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputSpendCreate.this);
				
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
				
				AlertDialog cancelpopup = cancelaction.create();
				cancelpopup.setTitle("경고");
				cancelpopup.setMessage("지출 기록을 그만 하시겠습니까?");
				cancelpopup.show();
				
			}
		});
		
		
		
		
		
	}
	// 뒤로가기 버튼 눌렀을 시
	@Override
	public void onBackPressed() {
		
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputSpendCreate.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InputSpendCreate.super.onBackPressed();
			}
		});
		
		AlertDialog cancelpopup = cancelaction.create();
		cancelpopup.setTitle("TEST");
		cancelpopup.setMessage("지출 기록을 그만 하시겠습니까?");
		cancelpopup.show();
	}
	
	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}
	
	private void updateLabel(){
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		
		datepicker.setText(sdf.format(myCalendar.getTime()));
	}
	
	
}