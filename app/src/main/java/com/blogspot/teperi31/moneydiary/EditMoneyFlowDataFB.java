package com.blogspot.teperi31.moneydiary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import java.util.Map;

public class EditMoneyFlowDataFB extends AppCompatActivity {
	
	TextView EditDateString;
	AppCompatSpinner EditAccount;
	AppCompatSpinner EditCategory;
	EditText EditPrice;
	EditText EditUsage;
	int mfListPosition;
	Date EditDate;
	Toolbar mToolbar;
	String EditType;
	
	MaterialButton EditButton;
	
	MaterialButton TypeIncome;
	MaterialButton TypeExpense;
	MaterialButton TypeTransfer;
	
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
		
		// 인텐트에서 보낸 키 값 가져오기
		// 없을 경우 에러 발생
		mMFListKey = getIntent().getStringExtra(EXTRA_MFDATA_KEY);
		if (mMFListKey == null) {
			throw new IllegalArgumentException("Key 값이 존재하지 않습니다.");
		}
		
		// Key 값을 기반으로 데이터 연결
		mMFListReference = FirebaseDatabase.getInstance().getReference()
				.child("moneyflow").child(user.getUid()).child(mMFListKey);
		
		// View 부분 연결
		EditDateString = findViewById(R.id.input_moneyflow_datepick);
		EditAccount = findViewById(R.id.input_moneyflow_accountspinner);
		EditCategory = findViewById(R.id.input_moneyflow_categoryspinner);
		EditPrice = findViewById(R.id.input_moneyflow_price);
		EditUsage = findViewById(R.id.input_moneyflow_content_text);
		
		TypeIncome = findViewById(R.id.input_moneyflow_type_income);
		TypeExpense = findViewById(R.id.input_moneyflow_type_expense);
		TypeTransfer = findViewById(R.id.input_moneyflow_type_Transfer);
		
		EditButton = findViewById(R.id.input_moneyflow_save);
		
		// 툴바 연결 & 뒤로가기 버튼 생성
		mToolbar = findViewById(R.id.input_moneyflow_toolbarTop);
		setSupportActionBar(mToolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		// Type 선택시 몇몇 변수를 바꿔주는 로직 설정
		// 분류 스피너를 바꿔주면 됨
		// 이체의 경우 분류 스피너를 입금계좌로 바꿈
		TypeIncome.setOnClickListener(new View.OnClickListener() {
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
				
				// 버튼 색 및 글씨 색 변경
				TypeIncome.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.colorAccent));
				TypeIncome.setTextColor(ContextCompat.getColorStateList(v.getContext(), R.color.colorBackground));
				TypeExpense.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.colorBackground));
				TypeExpense.setTextColor(ContextCompat.getColorStateList(v.getContext(), R.color.colorBlack));
				TypeTransfer.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.colorBackground));
				TypeTransfer.setTextColor(ContextCompat.getColorStateList(v.getContext(), R.color.colorBlack));
			}
		});
		
		TypeExpense.setOnClickListener(new View.OnClickListener() {
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
				
				// 버튼 색 및 글씨 색 변경
				TypeExpense.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.colorAccent));
				TypeExpense.setTextColor(ContextCompat.getColorStateList(v.getContext(), R.color.colorBackground));
				TypeIncome.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.colorBackground));
				TypeIncome.setTextColor(ContextCompat.getColorStateList(v.getContext(), R.color.colorBlack));
				TypeTransfer.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.colorBackground));
				TypeTransfer.setTextColor(ContextCompat.getColorStateList(v.getContext(), R.color.colorBlack));
				
			}
		});
		
		TypeTransfer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditType = "이체";
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				EditCategory.setAdapter(categoryAdapter);
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("출금");
				inputText.setText("입금");
				
				// 버튼 색 및 글씨 색 변경
				TypeTransfer.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.colorAccent));
				TypeTransfer.setTextColor(ContextCompat.getColorStateList(v.getContext(), R.color.colorBackground));
				TypeIncome.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.colorBackground));
				TypeIncome.setTextColor(ContextCompat.getColorStateList(v.getContext(), R.color.colorBlack));
				TypeExpense.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.colorBackground));
				TypeExpense.setTextColor(ContextCompat.getColorStateList(v.getContext(), R.color.colorBlack));
			}
		});
		
		
		EditButton.setText("수정하기");
		EditButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// 텍스트 가져오기
				final EditText inputPrice = findViewById(R.id.input_moneyflow_price);
				final EditText inputContent = findViewById(R.id.input_moneyflow_content_text);
				
				// 금액 확인 후 금액이 입력 안된 경우 금액 입력 요청
				
				if (inputPrice.getText().toString().trim().length() <= 0) {
					inputPrice.setError("금액을 입력하세요.");
					return;
				}
				// 이체의 경우 같은 계좌 이체를 막음
				else if (EditType.equals("이체") && EditAccount.getSelectedItem().toString().equals(EditCategory.getSelectedItem().toString())) {
					Toast.makeText(EditMoneyFlowDataFB.this, "같은 계좌로 이체할 수 없습니다", Toast.LENGTH_SHORT).show();
					return;
				}
				// 문제가 없을 경우 Save
				else {
					// Save 가 여러번 되는 것을 방지하기 위해 버튼 비활성화 및 로딩 바 생성
					findViewById(R.id.input_moneyflow_save).setEnabled(false);
					findViewById(R.id.input_moneyflow_progresslayout).setVisibility(View.VISIBLE);
					
					// 유저 상태 확인 후
					if (user == null) {
						Toast.makeText(EditMoneyFlowDataFB.this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
					} else {
						// Save 가 여러번 되는 것을 방지하기 위해 버튼 비활성화 및 로딩 바 생성
						findViewById(R.id.input_moneyflow_save).setEnabled(false);
						findViewById(R.id.input_moneyflow_progresslayout).setVisibility(View.VISIBLE);
						// 유저 상태 확인 후
						if (user == null) {
							Toast.makeText(EditMoneyFlowDataFB.this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
						} else {
							// 데이터 업데이트
							// 내용에 값이 없는 경우 카테고리 값 집어넣기
							if (EditUsage.length() <= 0) {
								EditUsage.setText(EditCategory.getSelectedItem().toString());
							}
							//1단계 데이터 맵으로 저장
							DataMoneyFlowFB EditData = new DataMoneyFlowFB(EditType,
									EditDate.getTime(),
									EditAccount.getSelectedItem().toString(),
									EditCategory.getSelectedItem().toString(),
									Long.parseLong(EditPrice.getText().toString()),
									EditUsage.getText().toString());
							
							Map<String, Object> InputValues = EditData.toMap();
							
							// 2단계 밸류값 업데이트 및 완료까지 기다리기
							mMFListReference.setValue(InputValues, new DatabaseReference.CompletionListener() {
								@Override
								public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
									findViewById(R.id.input_moneyflow_save).setEnabled(true);
									findViewById(R.id.input_moneyflow_progresslayout).setVisibility(View.GONE);
									finish();
								}
							});
						}
					}
					
					
				}
			}
		});
		
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// 데이터 가져오기
		ValueEventListener mfDataLinstener = new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				DataMoneyFlowFB mfData = dataSnapshot.getValue(DataMoneyFlowFB.class);
				// long 타입의 날짜 서식 지정
				String myFormat = "yyyy-MM-dd";
				SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
				
				// 날짜 받아오기
				EditDate = new Date(mfData.date);
				EditDateString.setText(sdf.format(EditDate));
				// DatePicker 연결
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
				// 계좌 아이템이 데이터 아이템으로 선택되도록 함
				EditAccount.setSelection(accountSelect);
				// 가격과 내용 받아오기
				EditPrice.setText(String.valueOf(mfData.price));
				EditUsage.setText(mfData.usage);
				
				// 타입이 지출일 경우
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
					
					// 버튼 색 및 글씨 색 변경
					TypeExpense.setBackgroundTintList(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorAccent));
					TypeExpense.setTextColor(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBackground));
					TypeIncome.setBackgroundTintList(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBackground));
					TypeIncome.setTextColor(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBlack));
					TypeTransfer.setBackgroundTintList(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBackground));
					TypeTransfer.setTextColor(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBlack));
					
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
					// 버튼 색 및 글씨 색 변경
					TypeIncome.setBackgroundTintList(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorAccent));
					TypeIncome.setTextColor(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBackground));
					TypeExpense.setBackgroundTintList(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBackground));
					TypeExpense.setTextColor(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBlack));
					TypeTransfer.setBackgroundTintList(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBackground));
					TypeTransfer.setTextColor(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBlack));
					
					
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
					// 버튼 색 및 글씨 색 변경
					TypeTransfer.setBackgroundTintList(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorAccent));
					TypeTransfer.setTextColor(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBackground));
					TypeExpense.setBackgroundTintList(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBackground));
					TypeExpense.setTextColor(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBlack));
					TypeIncome.setBackgroundTintList(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBackground));
					TypeIncome.setTextColor(ContextCompat.getColorStateList(EditMoneyFlowDataFB.this, R.color.colorBlack));
				}
			}
			
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Toast.makeText(EditMoneyFlowDataFB.this, "로딩 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
		};
		mMFListReference.addValueEventListener(mfDataLinstener);
		
		
	}
	
	// 액션 바 뒤로가기 버튼을 눌렀을 시 바로 종료
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return super.onSupportNavigateUp();
	}
}

