package com.blogspot.teperi31.moneydiary;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class InputDairyCreate extends AppCompatActivity {
	Calendar myCalendar;
	TextView datepicker;
	DatePickerDialog.OnDateSetListener date;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcreatedairy);
		
		
		myCalendar = Calendar.getInstance(Locale.KOREA);
		
		// 툴바 입력
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop_basic);
		setActionBar(myToolbar);
		
		datepicker = findViewById(R.id.inputCreateDairyDateEdit);
		
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
				new DatePickerDialog(InputDairyCreate.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		
		
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
					Toast.makeText(InputDairyCreate.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
				} else if (inputContentText.getText().toString().length() <= 0) {
					Toast.makeText(InputDairyCreate.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
				} else if (Intent.ACTION_SEND.equals(action) && type.startsWith("image/")) {
					Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
					obj = new DataDairy(myCalendar,
							inputTitleText.getText().toString(),
							inputContentText.getText().toString(),
							0
							);
					ApplicationClass.dList.add(obj);
					
					Intent i = new Intent(InputDairyCreate.this, RecyclerviewDairy.class);
					// 스택 관리
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					startActivity(i);
				} else {
					obj = new DataDairy(myCalendar,
							inputTitleText.getText().toString(),
							inputContentText.getText().toString(),
							0
					);
					Intent i = new Intent(InputDairyCreate.this, RecyclerviewDairy.class);
					ApplicationClass.dList.add(obj);
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
				AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputDairyCreate.this);
				
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
		
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputDairyCreate.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InputDairyCreate.super.onBackPressed();
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
