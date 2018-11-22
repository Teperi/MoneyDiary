package com.blogspot.teperi31.moneydiary;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class EditDiarydata extends AppCompatActivity {
	TextView EditTitletext;
	TextView EditContenttext;
	TextView EditDate;
	ImageView EditImage;
	Uri setImage;
	Calendar myCalendar;
	Button EditCompleteButton;
	DatePickerDialog.OnDateSetListener date;
	int dListPosition;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcreatediary);
		
		EditTitletext = findViewById(R.id.inputCreateDiaryTitle);
		EditContenttext = findViewById(R.id.inputCreateDiaryContent);
		EditDate = findViewById(R.id.inputCreateDiaryDateEdit);
		EditImage = findViewById(R.id.inputCreateDiaryImage);
		EditCompleteButton = findViewById(R.id.inputCreateDiaryComplete);
		
		EditCompleteButton.setText("수정 완료");
		
		
		
		Collections.sort(ApplicationClass.dList, new Comparator<DataDiary>() {
			@Override
			public int compare(DataDiary o1, DataDiary o2) {
				return o2.DListDate.compareTo(o1.DListDate);
			}
		});
		
		Intent intent = getIntent();
		dListPosition = intent.getIntExtra("position", -1);
		
		if(dListPosition == -1) {
			Toast.makeText(this, "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
		} else {
			EditTitletext.setText(ApplicationClass.dList.get(dListPosition).DListTitle);
			EditContenttext.setText(ApplicationClass.dList.get(dListPosition).DListContent);
			EditImage.setImageURI(ApplicationClass.dList.get(dListPosition).DListImage);
			setImage = ApplicationClass.dList.get(dListPosition).DListImage;
			String myFormat = "yyyy-MM-dd";
			SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
			EditDate.setText(sdf.format(ApplicationClass.dList.get(dListPosition).DListDate.getTime()));
			myCalendar = ApplicationClass.dList.get(dListPosition).DListDate;
			
		}
		
		// 선택한 날짜로 업데이트하는 함수 적용
		date = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
				myCalendar.set(year, month, dayOfMonth);
				updateLabel();
			}
		};
		
		// 선택 팝업 열기
		EditDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(EditDiarydata.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		
		EditCompleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// 데이터를 집어넣기 위한 Object 생성
				DataDiary obj = null;
				
				if (EditTitletext.getText().toString().length() <= 0) {
					Toast.makeText(EditDiarydata.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
				} else if (EditContenttext.getText().toString().length() <= 0) {
					Toast.makeText(EditDiarydata.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
				} else if (setImage == null) {
					obj = new DataDiary(myCalendar,
							EditTitletext.getText().toString(),
							EditContenttext.getText().toString(),
							null
					);
					// 이전 기록 삭제
					ApplicationClass.dList.remove(dListPosition);
					
					// 수정 완료된 기록 집어넣기
					Intent i = new Intent(EditDiarydata.this, RecyclerviewDiary.class);
					ApplicationClass.dList.add(obj);
					// 스택 관리
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					
					startActivity(i);
				} else {
					obj = new DataDiary(myCalendar,
							EditTitletext.getText().toString(),
							EditContenttext.getText().toString(),
							setImage
					);
					// 이전 기록 삭제
					ApplicationClass.dList.remove(dListPosition);
					
					// 수정 완료된 기록 집어넣기
					Intent i = new Intent(EditDiarydata.this, RecyclerviewDiary.class);
					ApplicationClass.dList.add(obj);
					// 스택 관리
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					
					startActivity(i);
				}
			}
		});
		
		// 취소 버튼 클릭시 팝업 띄움
		// 뒤로가기 버튼에도 같은 기능 구현
		findViewById(R.id.inputCreateDiaryCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancelaction = new AlertDialog.Builder(EditDiarydata.this);
				
				cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				
				cancelaction.setNegativeButton("중단", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				
				AlertDialog cancelpopup = cancelaction.create();
				cancelpopup.setTitle("경고");
				cancelpopup.setMessage("다이어리 수정을 그만 하시겠습니까?");
				cancelpopup.show();
				
			}
		});
	}
	
	// 뒤로가기 버튼 눌렀을 시
	@Override
	public void onBackPressed() {
		
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(EditDiarydata.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		cancelaction.setNegativeButton("중단", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditDiarydata.super.onBackPressed();
			}
		});
		
		AlertDialog cancelpopup = cancelaction.create();
		cancelpopup.setTitle("TEST");
		cancelpopup.setMessage("지출 기록을 그만 하시겠습니까?");
		cancelpopup.show();
	}
	
	// 이 페이지가 스텍에 쌓이지 않도록 종료
	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}
	
	private void updateLabel() {
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		
		EditDate.setText(sdf.format(myCalendar.getTime()));
	}
}
