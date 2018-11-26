package com.blogspot.teperi31.moneydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class InputMoneyFlowCreateTransfer extends AppCompatActivity {
	
	Date setDate;
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_moneyflow);
		
		// 앱바 생성
		Toolbar mToolbar = findViewById(R.id.input_moneyflow_toolbarTop);
		setSupportActionBar(mToolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		// 날짜 설정 view 연결 및 Datepick popup 생성
		View mDatepick = findViewById(R.id.input_moneyflow_datepick);
		
		setDate = UtilDateTimePicker.setTodayDate(mDatepick);
		setDate = UtilDateTimePicker.setDatepopup(this, setDate, mDatepick);
		
		// 출금 입금 text 변경
		
		TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
		TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
		outputText.setText("출금");
		inputText.setText("입금");
		
		//출금계좌 스피너
		final AppCompatSpinner spinnerAccount = findViewById(R.id.input_moneyflow_accountspinner);
		ArrayAdapter accountAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
		accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAccount.setAdapter(accountAdapter);
		
		//입금계좌 스피너
		final AppCompatSpinner spinnerCategory = findViewById(R.id.input_moneyflow_categoryspinner);
		spinnerCategory.setAdapter(accountAdapter);
		
		
		// 수입 지출 이체 버튼 변경
		Button mButtonClicked= findViewById(R.id.input_moneyflow_type_Transfer);
		mButtonClicked.setBackgroundColor(getColor(R.color.colorAccent));
		mButtonClicked.setTextColor(getColor(R.color.colorBackground));
		
		findViewById(R.id.input_moneyflow_type_expense).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(InputMoneyFlowCreateTransfer.this, InputMoneyFlowCreateExpense.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(i);
				
			}
		});
		
		findViewById(R.id.input_moneyflow_type_income).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(InputMoneyFlowCreateTransfer.this, InputMoneyFlowCreateIncome.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(i);
				
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
					Toast.makeText(InputMoneyFlowCreateTransfer.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
					
				} else if (spinnerAccount.getSelectedItem().toString().equals(spinnerCategory.getSelectedItem().toString())) {
					Toast.makeText(InputMoneyFlowCreateTransfer.this, "같은 계좌로 이체할 수 없습니다", Toast.LENGTH_SHORT).show();
					
				} else if (inputContent.getText().toString().length() <= 0) {
					ApplicationClass.mfList.add(
							new DataMoneyFlow("이체",
									setDate,
									spinnerAccount.getSelectedItem().toString(),
									spinnerCategory.getSelectedItem().toString(),
									Integer.parseInt(inputPrice.getText().toString().trim()),
									spinnerCategory.getSelectedItem().toString(),
									ApplicationClass.mfList.size()
							));
					UtilPreference.setMoneyflow(InputMoneyFlowCreateTransfer.this);
					Intent i = new Intent(InputMoneyFlowCreateTransfer.this, RecyclerviewMoneyFlow.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				} else {
					ApplicationClass.mfList.add(
							new DataMoneyFlow("이체",
									setDate,
									spinnerAccount.getSelectedItem().toString(),
									spinnerCategory.getSelectedItem().toString(),
									Integer.parseInt(inputPrice.getText().toString().trim()),
									inputContent.getText().toString(),
									ApplicationClass.mfList.size()
							));
					UtilPreference.setMoneyflow(InputMoneyFlowCreateTransfer.this);
					Intent i = new Intent(InputMoneyFlowCreateTransfer.this, RecyclerviewMoneyFlow.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
				
			}
		});
		
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topbar_input, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.topbar_input_setting:
				//TODO : 설정 창 만들어서 분류 변경 가능하도록 허용
				Toast.makeText(this, "설정 창 이동", Toast.LENGTH_SHORT).show();
			default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}
	
	
	// 뒤로가기 버튼 눌렀을 시
	@Override
	public void onBackPressed() {
		
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputMoneyFlowCreateTransfer.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InputMoneyFlowCreateTransfer.super.onBackPressed();
			}
		});
		
		AlertDialog cancelpopup = cancelaction.create();
		cancelpopup.setTitle("경고");
		cancelpopup.setMessage("이체 기록을 그만 하시겠습니까?");
		cancelpopup.show();
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(InputMoneyFlowCreateTransfer.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			
			}
			
		});
		
		cancelaction.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InputMoneyFlowCreateTransfer.super.onBackPressed();
			}
		});
		
		AlertDialog cancelpopup = cancelaction.create();
		cancelpopup.setTitle("경고");
		cancelpopup.setMessage("이체 기록을 그만 하시겠습니까?");
		cancelpopup.show();
		return true;
	}
}
