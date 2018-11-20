package com.blogspot.teperi31.moneydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class DairyCreateInput extends AppCompatActivity {
	
	AppCompatSpinner sp_year;
	AppCompatSpinner sp_month;
	AppCompatSpinner sp_day;
	ArrayList<Integer> yearselect;
	ArrayList<Integer> monthselect;
	ArrayList<Integer> dayselect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcreatedairy);
		
		// 툴바 입력
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
		
		
		final Intent intent = getIntent();
		final String action = intent.getAction();
		final String type = intent.getType();
		
		
		EditText sharecontent = findViewById(R.id.inputCreateDairyContent);
		final ImageView shareimage = findViewById(R.id.inputCreateDairyImage);
		
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
				if (sharedText != null) {
					sharecontent.setText(sharedText);
				}
				
			} else if (type.startsWith("image/")) {
				
				Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
				if (imageUri != null) {
					shareimage.setImageURI(imageUri);
				}
			}
		}
		
		
		
		findViewById(R.id.inputCreateDairyComplete).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText inputTitleText = findViewById(R.id.inputCreateDairyTitle);
				EditText inputContentText = findViewById(R.id.inputCreateDairyContent);
				
				DataDairy obj = null;
				
				if (inputTitleText.getText().toString().length() <=0) {
					Toast.makeText(DairyCreateInput.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
				} else if (inputContentText.getText().toString().length() <= 0) {
					Toast.makeText(DairyCreateInput.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
				} else if (Intent.ACTION_SEND.equals(action) && type.startsWith("image/")) {
					Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
					obj = new DataDairy(Integer.parseInt(sp_year.getSelectedItem().toString()),
							Integer.parseInt(sp_month.getSelectedItem().toString()),
							Integer.parseInt(sp_day.getSelectedItem().toString()),
							inputTitleText.getText().toString(),
							inputContentText.getText().toString(),
							0
							);
					Log.d("test","ifffff");
					Intent i = new Intent(DairyCreateInput.this, RecyclerviewDairy.class);
					i.putExtra("InputCreateDairy", obj);
					// 스택 관리
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					startActivity(i);
				} else {
					obj = new DataDairy(Integer.parseInt(sp_year.getSelectedItem().toString()),
							Integer.parseInt(sp_month.getSelectedItem().toString()),
							Integer.parseInt(sp_day.getSelectedItem().toString()),
							inputTitleText.getText().toString(),
							inputContentText.getText().toString(),
							0
					);
					Log.d("test","eslse");
					Intent i = new Intent(DairyCreateInput.this, RecyclerviewDairy.class);
					i.putExtra("InputCreateDairy", obj);
					// 스택 관리
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					startActivity(i);
				}
				
			
			}
		});
		
		// 취소 버튼 클릭시 팝업 띄움
		// 뒤로가기 버튼에도 같은 기능 구현
		findViewById(R.id.inputCreateDairyCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancelaction = new AlertDialog.Builder(DairyCreateInput.this);
				
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
				cancelpopup.setMessage("다이어리 기록을 그만 하시겠습니까?");
				cancelpopup.show();
				
			}
		});
	}
	// 뒤로가기 버튼 눌렀을 시
	@Override
	public void onBackPressed() {
		
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(DairyCreateInput.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DairyCreateInput.super.onBackPressed();
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
}
