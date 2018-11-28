package com.blogspot.teperi31.moneydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
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

public class EditMoneyFlowData extends AppCompatActivity {
	
	
	TextView EditDateString;
	AppCompatSpinner EditAccount;
	AppCompatSpinner EditCategory;
	EditText EditPrice;
	EditText EditContent;
	int mfListPosition;
	Date EditDate;
	Toolbar mToolbar;
	String EditType;
	
	ArrayAdapter categoryAdapter;
	
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_moneyflow);
		
		mToolbar = findViewById(R.id.input_moneyflow_toolbarTop);
		setSupportActionBar(mToolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		
		
		
		EditDateString = findViewById(R.id.input_moneyflow_datepick);
		EditAccount = findViewById(R.id.input_moneyflow_accountspinner);
		EditCategory = findViewById(R.id.input_moneyflow_categoryspinner);
		EditPrice = findViewById(R.id.input_moneyflow_price);
		EditContent = findViewById(R.id.input_moneyflow_content_text);
		
		
		
		
		// 포지션 값 받아오기
		Intent intent = getIntent();
		mfListPosition = intent.getIntExtra("mfposition", -1);
		
		if (mfListPosition == -1) {
			Toast.makeText(this, "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
		} else {
			EditDateString.setText(ApplicationClass.mfList.get(mfListPosition).MFListDateString);
			EditDate =  ApplicationClass.mfList.get(mfListPosition).MFListDate;
			UtilDateTimePicker.setDatepopup(this, EditDate, EditDateString);
			
			//계좌 스피너
			ArrayAdapter accountAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
			accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			EditAccount.setAdapter(accountAdapter);
			
			int accountSelect = 0;
			for (int i = 0; i < ApplicationClass.mfAccountList.size(); i++) {
				if (ApplicationClass.mfAccountList.get(i).equals(ApplicationClass.mfList.get(mfListPosition).MFListAccount)) {
					accountSelect = i;
				}
			}
			EditAccount.setSelection(accountSelect);
			EditPrice.setText(String.valueOf(ApplicationClass.mfList.get(mfListPosition).MFListPrice));
			EditContent.setText(ApplicationClass.mfList.get(mfListPosition).MFListUsage);
			
			if (ApplicationClass.mfList.get(mfListPosition).MFListType.equals("지출")) {
				
				EditType = "지출";
				
				//분류 스피너
				categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ApplicationClass.mfExpenseCategoryList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				EditCategory.setAdapter(categoryAdapter);
				
				int categorySelect = 0;
				for (int i = 0; i < ApplicationClass.mfExpenseCategoryList.size(); i++) {
					if (ApplicationClass.mfExpenseCategoryList.get(i).equals(ApplicationClass.mfList.get(mfListPosition).MFListCategory)) {
						categorySelect = i;
					}
				}
				EditCategory.setSelection(categorySelect);
				findViewById(R.id.input_moneyflow_type_expense).setBackgroundColor(getColor(R.color.colorAccent));
				
			} else if(ApplicationClass.mfList.get(mfListPosition).MFListType.equals("수입")){
				
				EditType = "수입";
				
				//분류 스피너
				categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ApplicationClass.mfIncomeCategoryList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				EditCategory.setAdapter(categoryAdapter);
				
				int categorySelect = 0;
				for (int i = 0; i < ApplicationClass.mfIncomeCategoryList.size(); i++) {
					if (ApplicationClass.mfIncomeCategoryList.get(i).equals(ApplicationClass.mfList.get(mfListPosition).MFListCategory)) {
						categorySelect = i;
					}
				}
				EditCategory.setSelection(categorySelect);
				findViewById(R.id.input_moneyflow_type_income).setBackgroundColor(getColor(R.color.colorAccent));
				
				
			} else {
				EditType = "이체";
				//분류 스피너
				categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				EditCategory.setAdapter(categoryAdapter);
				
				int categorySelect = 0;
				for (int i = 0; i < ApplicationClass.mfAccountList.size(); i++) {
					if (ApplicationClass.mfAccountList.get(i).equals(ApplicationClass.mfList.get(mfListPosition).MFListCategory)) {
						categorySelect = i;
					}
				}
				EditCategory.setSelection(categorySelect);
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("출금");
				inputText.setText("입금");
				findViewById(R.id.input_moneyflow_type_Transfer).setBackgroundColor(getColor(R.color.colorAccent));
			}
		}
		
		findViewById(R.id.input_moneyflow_type_income).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				EditType = "수입";
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("계좌");
				inputText.setText("분류");
				
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfIncomeCategoryList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				EditCategory.setAdapter(categoryAdapter);
				
				int categorySelect = 0;
				for (int i = 0; i < ApplicationClass.mfIncomeCategoryList.size(); i++) {
					if (ApplicationClass.mfIncomeCategoryList.get(i).equals(ApplicationClass.mfList.get(mfListPosition).MFListCategory)) {
						categorySelect = i;
					}
				}
				EditCategory.setSelection(categorySelect);
				findViewById(R.id.input_moneyflow_type_income).setBackgroundColor(getColor(R.color.colorAccent));
				findViewById(R.id.input_moneyflow_type_expense).setBackgroundColor(getColor(R.color.colorBackground));
				findViewById(R.id.input_moneyflow_type_Transfer).setBackgroundColor(getColor(R.color.colorBackground));
			}
		});
		
		findViewById(R.id.input_moneyflow_type_expense).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				EditType = "지출";
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("계좌");
				inputText.setText("분류");
				
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfExpenseCategoryList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				EditCategory.setAdapter(categoryAdapter);
				
				int categorySelect = 0;
				for (int i = 0; i < ApplicationClass.mfExpenseCategoryList.size(); i++) {
					if (ApplicationClass.mfExpenseCategoryList.get(i).equals(ApplicationClass.mfList.get(mfListPosition).MFListCategory)) {
						categorySelect = i;
					}
				}
				EditCategory.setSelection(categorySelect);
				findViewById(R.id.input_moneyflow_type_expense).setBackgroundColor(getColor(R.color.colorAccent));
				
				findViewById(R.id.input_moneyflow_type_income).setBackgroundColor(getColor(R.color.colorBackground));
				findViewById(R.id.input_moneyflow_type_Transfer).setBackgroundColor(getColor(R.color.colorBackground));
				
			}
		});
		
		findViewById(R.id.input_moneyflow_type_Transfer).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditType = "이체";
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				EditCategory.setAdapter(categoryAdapter);
				
				int categorySelect = 0;
				for (int i = 0; i < ApplicationClass.mfAccountList.size(); i++) {
					if (ApplicationClass.mfAccountList.get(i).equals(ApplicationClass.mfList.get(mfListPosition).MFListCategory)) {
						categorySelect = i;
					}
				}
				EditCategory.setSelection(categorySelect);
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("출금");
				inputText.setText("입금");
				findViewById(R.id.input_moneyflow_type_Transfer).setBackgroundColor(getColor(R.color.colorAccent));
				findViewById(R.id.input_moneyflow_type_expense).setBackgroundColor(getColor(R.color.colorBackground));
				findViewById(R.id.input_moneyflow_type_income).setBackgroundColor(getColor(R.color.colorBackground));
			}
		});
		
		findViewById(R.id.input_moneyflow_save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(EditPrice.getText().toString().trim().length() <= 0) {
					Toast.makeText(EditMoneyFlowData.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
				} else if (EditType.equals("이체") && EditAccount.getSelectedItem().toString().equals(EditCategory.getSelectedItem().toString())) {
					Toast.makeText(EditMoneyFlowData.this, "같은 계좌로 이체할 수 없습니다", Toast.LENGTH_SHORT).show();
				} else if (EditContent.getText().toString().length() <= 0){
					
					ApplicationClass.mfList.add(
							new DataMoneyFlow(EditType,
									EditDate,
									EditAccount.getSelectedItem().toString(),
									EditCategory.getSelectedItem().toString(),
									Integer.parseInt(EditPrice.getText().toString().trim()),
									EditCategory.getSelectedItem().toString(),
									ApplicationClass.mfList.size())
					);
					
					ApplicationClass.mfList.remove(mfListPosition);
					
					UtilPreference.setMoneyflow(EditMoneyFlowData.this);
					
					Intent i = new Intent(EditMoneyFlowData.this, RecyclerviewMoneyFlow.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				} else {
					ApplicationClass.mfList.add(
							new DataMoneyFlow(EditType,
									EditDate,
									EditAccount.getSelectedItem().toString(),
									EditCategory.getSelectedItem().toString(),
									Integer.parseInt(EditPrice.getText().toString().trim()),
									EditContent.getText().toString(),
									ApplicationClass.mfList.size())
					);
					
					ApplicationClass.mfList.remove(mfListPosition);
					
					UtilPreference.setMoneyflow(EditMoneyFlowData.this);
					
					Intent i = new Intent(EditMoneyFlowData.this, RecyclerviewMoneyFlow.class);
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
	
	
	
	
	// 뒤로가기 버튼 눌렀을 시
	@Override
	public void onBackPressed() {
		
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(EditMoneyFlowData.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		cancelaction.setNegativeButton("중단", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditMoneyFlowData.super.onBackPressed();
			}
		});
		
		AlertDialog cancelpopup = cancelaction.create();
		cancelpopup.setTitle("경고");
		cancelpopup.setMessage("수정을 중단하겠습니까?");
		cancelpopup.show();
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		AlertDialog.Builder cancelaction = new AlertDialog.Builder(EditMoneyFlowData.this);
		
		cancelaction.setPositiveButton("계속 입력", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			
			}
			
		});
		
		cancelaction.setNegativeButton("중단", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditMoneyFlowData.super.onBackPressed();
			}
		});
		
		AlertDialog cancelpopup = cancelaction.create();
		cancelpopup.setTitle("경고");
		cancelpopup.setMessage("수정을 중단하겠습니까?");
		cancelpopup.show();
		return true;
	}
}
