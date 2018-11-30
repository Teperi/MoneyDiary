package com.blogspot.teperi31.moneydiary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditMoneyFlowDataFB extends AppCompatActivity {
	
	TextView EditDateString;
	AppCompatSpinner EditAccount;
	AppCompatSpinner EditCategory;
	EditText EditPrice;
	EditText EditContent;
	int mfListPosition;
	Date EditDate;
	Toolbar mToolbar;
	String EditType;
	
	ArrayAdapter accountAdapter;
	ArrayAdapter categoryAdapter;
	
	private FirebaseUser user;
	private String mMFListKey;
	private DatabaseReference mMFListReference;
	private ValueEventListener mMFListListener;
	
	public static final String EXTRA_MFDATA_KEY = "mflist_key";
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_moneyflow);
		
		// 유저 정보 가져오기
		// 유저가 없는 경우 error 발생
		user = FirebaseAuth.getInstance().getCurrentUser();
		if (user == null) {
			throw new IllegalArgumentException("로그인한 유저가 아닙니다.");
		}
		
		mMFListKey = getIntent().getStringExtra(EXTRA_MFDATA_KEY);
		if (mMFListKey == null) {
			throw new IllegalArgumentException("Key 값이 존재하지 않습니다.");
		}
		
		mMFListReference = FirebaseDatabase.getInstance().getReference()
				.child("moneyflow").child(user.getUid()).child(mMFListKey);
		
		EditDateString = findViewById(R.id.input_moneyflow_datepick);
		EditAccount = findViewById(R.id.input_moneyflow_accountspinner);
		EditCategory = findViewById(R.id.input_moneyflow_categoryspinner);
		EditPrice = findViewById(R.id.input_moneyflow_price);
		EditContent = findViewById(R.id.input_moneyflow_content_text);
		
		mToolbar = findViewById(R.id.input_moneyflow_toolbarTop);
		setSupportActionBar(mToolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		ValueEventListener mfDataLinstener = new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				DataMoneyFlowFB mfData = dataSnapshot.getValue(DataMoneyFlowFB.class);
				
				String myFormat = "yyyy-MM-dd";
				SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
				
				EditDate = new Date(mfData.date);
				EditDateString.setText(sdf.format(EditDate));
				
				UtilDateTimePicker.setDatepopup(EditMoneyFlowDataFB.this, EditDate, EditDateString);
				
				//계좌 스피너
				accountAdapter = new ArrayAdapter(EditMoneyFlowDataFB.this, android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
				accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				EditAccount.setAdapter(accountAdapter);
				
				int accountSelect = 0;
				for (int i = 0; i < ApplicationClass.mfAccountList.size(); i++) {
					if (ApplicationClass.mfAccountList.get(i).equals(mfData.account)) {
						accountSelect = i;
					}
				}
				EditAccount.setSelection(accountSelect);
				EditPrice.setText(String.valueOf(mfData.price));
				EditContent.setText(mfData.usage);
				
				if (mfData.type.equals("지출")) {
					
					EditType = "지출";
					
					//분류 스피너
					categoryAdapter = new ArrayAdapter(EditMoneyFlowDataFB.this, android.R.layout.simple_spinner_item, ApplicationClass.mfExpenseCategoryList);
					categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					EditCategory.setAdapter(categoryAdapter);
					
					int categorySelect = 0;
					for (int i = 0; i < ApplicationClass.mfExpenseCategoryList.size(); i++) {
						if (ApplicationClass.mfExpenseCategoryList.get(i).equals(mfData.category)) {
							categorySelect = i;
						}
					}
					EditCategory.setSelection(categorySelect);
					findViewById(R.id.input_moneyflow_type_expense).setBackgroundColor(getColor(R.color.colorAccent));
					
				} else if (mfData.type.equals("수입")) {
					
					EditType = "수입";
					
					//분류 스피너
					categoryAdapter = new ArrayAdapter(EditMoneyFlowDataFB.this, android.R.layout.simple_spinner_item, ApplicationClass.mfIncomeCategoryList);
					categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					EditCategory.setAdapter(categoryAdapter);
					
					int categorySelect = 0;
					for (int i = 0; i < ApplicationClass.mfIncomeCategoryList.size(); i++) {
						if (ApplicationClass.mfIncomeCategoryList.get(i).equals(mfData.category)) {
							categorySelect = i;
						}
					}
					EditCategory.setSelection(categorySelect);
					findViewById(R.id.input_moneyflow_type_income).setBackgroundColor(getColor(R.color.colorAccent));
					
					
				} else {
					EditType = "이체";
					//분류 스피너
					categoryAdapter = new ArrayAdapter(EditMoneyFlowDataFB.this, android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
					categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					EditCategory.setAdapter(categoryAdapter);
					
					int categorySelect = 0;
					for (int i = 0; i < ApplicationClass.mfAccountList.size(); i++) {
						if (ApplicationClass.mfAccountList.get(i).equals(mfData.category)) {
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
			
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Toast.makeText(EditMoneyFlowDataFB.this, "로딩 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
		};
		mMFListReference.addValueEventListener(mfDataLinstener);
	}
	
}

