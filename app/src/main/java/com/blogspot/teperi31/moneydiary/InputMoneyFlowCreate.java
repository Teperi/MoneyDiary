package com.blogspot.teperi31.moneydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class InputMoneyFlowCreate extends AppCompatActivity {
	
	Date setDate;
	String setType;
	AppCompatSpinner spinnerCategory;
	ArrayAdapter categoryAdapter;
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moneyflow_input);
		
		// 앱바 생성
		Toolbar mToolbar = findViewById(R.id.input_moneyflow_toolbarTop);
		setSupportActionBar(mToolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		// 날짜 설정 view 연결 및 Datepick popup 생성
		TextView mDatepick = findViewById(R.id.input_moneyflow_datepick);
		
		
		UtilDateTimePicker.setTodayDate(mDatepick);
		setDate = new Date();
		UtilDateTimePicker.setDatepopup(this, setDate, mDatepick);
		
		
		
		//계좌 스피너
		final AppCompatSpinner spinnerAccount = findViewById(R.id.input_moneyflow_accountspinner);
		ArrayAdapter accountAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
		accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAccount.setAdapter(accountAdapter);
		
		//분류 스피너
		spinnerCategory = findViewById(R.id.input_moneyflow_categoryspinner);
		categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ApplicationClass.mfExpenseCategoryList);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCategory.setAdapter(categoryAdapter);
		
		setType = "지출";
		findViewById(R.id.input_moneyflow_type_expense).setBackgroundColor(getColor(R.color.colorAccentLight));
		
		findViewById(R.id.input_moneyflow_type_income).setBackgroundColor(getColor(R.color.colorBackground));
		findViewById(R.id.input_moneyflow_type_Transfer).setBackgroundColor(getColor(R.color.colorBackground));
		
		// Type 선택시 몇몇 변수를 바꿔주는 로직 설정
		// 분류 스피너를 바꿔주면 됨
		// 이체의 경우 분류 스피너를 입금계좌로 바꿈
		// 버튼 색 변경
		findViewById(R.id.input_moneyflow_type_income).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				setType = "수입";
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("계좌");
				inputText.setText("분류");
				
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfIncomeCategoryList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerCategory.setAdapter(categoryAdapter);
				
				findViewById(R.id.input_moneyflow_type_income).setBackgroundColor(getColor(R.color.colorAccentLight));
				findViewById(R.id.input_moneyflow_type_expense).setBackgroundColor(getColor(R.color.colorBackground));
				findViewById(R.id.input_moneyflow_type_Transfer).setBackgroundColor(getColor(R.color.colorBackground));
			}
		});
		
		findViewById(R.id.input_moneyflow_type_expense).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				setType = "지출";
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("계좌");
				inputText.setText("분류");
				
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfExpenseCategoryList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerCategory.setAdapter(categoryAdapter);
				
				
				findViewById(R.id.input_moneyflow_type_expense).setBackgroundColor(getColor(R.color.colorAccentLight));
				
				findViewById(R.id.input_moneyflow_type_income).setBackgroundColor(getColor(R.color.colorBackground));
				findViewById(R.id.input_moneyflow_type_Transfer).setBackgroundColor(getColor(R.color.colorBackground));
				
			}
		});
		
		findViewById(R.id.input_moneyflow_type_Transfer).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				setType = "이체";
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerCategory.setAdapter(categoryAdapter);
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("출금");
				inputText.setText("입금");
				findViewById(R.id.input_moneyflow_type_Transfer).setBackgroundColor(getColor(R.color.colorAccentLight));
				findViewById(R.id.input_moneyflow_type_expense).setBackgroundColor(getColor(R.color.colorBackground));
				findViewById(R.id.input_moneyflow_type_income).setBackgroundColor(getColor(R.color.colorBackground));
			}
		});
		
		// 저장하기
		findViewById(R.id.input_moneyflow_save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// 텍스트 가져오기
				EditText inputPrice = findViewById(R.id.input_moneyflow_price);
				EditText inputContent = findViewById(R.id.input_moneyflow_content_text);
				
				// 금액 확인 후 금액이 입력 안된 경우 금액 입력 요청
				
				if(inputPrice.getText().toString().trim().length() <= 0) {
					Toast.makeText(InputMoneyFlowCreate.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
					
				}
				// 이체의 경우 같은 계좌 이체를 막음
				else if (setType.equals("이체") && spinnerAccount.getSelectedItem().toString().equals(spinnerCategory.getSelectedItem().toString())) {
					Toast.makeText(InputMoneyFlowCreate.this, "같은 계좌로 이체할 수 없습니다", Toast.LENGTH_SHORT).show();
					
				}
				// 내용을 안쓴 경우 분류의 내용을 적어줌
				else if (inputContent.getText().toString().length() <= 0) {
					ApplicationClass.mfList.add(
							new DataMoneyFlow(setType,
									setDate,
									spinnerAccount.getSelectedItem().toString(),
									spinnerCategory.getSelectedItem().toString(),
									Integer.parseInt(inputPrice.getText().toString().trim()),
									spinnerCategory.getSelectedItem().toString(),
									ApplicationClass.mfList.size()
									));
					UtilPreference.setMoneyflow(InputMoneyFlowCreate.this);
					Intent i = new Intent(InputMoneyFlowCreate.this, RecyclerviewMoneyFlow.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
				// 내용을 쓸 경우 전체 저장
				else {
					ApplicationClass.mfList.add(
							new DataMoneyFlow("지출",
									setDate,
									spinnerAccount.getSelectedItem().toString(),
									spinnerCategory.getSelectedItem().toString(),
									Integer.parseInt(inputPrice.getText().toString().trim()),
									inputContent.getText().toString(),
									ApplicationClass.mfList.size()
							));
					UtilPreference.setMoneyflow(InputMoneyFlowCreate.this);
					Intent i = new Intent(InputMoneyFlowCreate.this, RecyclerviewMoneyFlow.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
				
			}
		});
		
		
	}
	
	// 스택 안쌓이도록 Pause 에서 세이브
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
	/*// 옵션 메뉴를 만듬
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topbar_input, menu);
		return true;
	}
	
	// 계좌 및 분류 설정을 저장하는 설정 창 이동
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.topbar_input_setting:
				Toast.makeText(this, "설정 창 이동", Toast.LENGTH_SHORT).show();
			default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}*/
	
	
	// 뒤로가기 버튼 눌렀을 시
	@Override
	public void onBackPressed() {
		
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputMoneyFlowCreate.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InputMoneyFlowCreate.super.onBackPressed();
			}
		});
		
		AlertDialog cancelpopup = cancelaction.create();
		cancelpopup.setTitle("경고");
		cancelpopup.setMessage("기록을 지우겠습니까?");
		cancelpopup.show();
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputMoneyFlowCreate.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			
			}
			
		});
		
		cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InputMoneyFlowCreate.super.onBackPressed();
			}
		});
		
		AlertDialog cancelpopup = cancelaction.create();
		cancelpopup.setTitle("경고");
		cancelpopup.setMessage("기록을 지우겠습니까?");
		cancelpopup.show();
		return true;
	}
}
