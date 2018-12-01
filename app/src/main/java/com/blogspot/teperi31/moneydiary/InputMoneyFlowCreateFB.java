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

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InputMoneyFlowCreateFB extends AppCompatActivity {
	Date setDate;
	String setType;
	AppCompatSpinner spinnerAccount;
	ArrayAdapter accountAdapter;
	AppCompatSpinner spinnerCategory;
	ArrayAdapter categoryAdapter;
	
	MaterialButton TypeIncome;
	MaterialButton TypeExpense;
	MaterialButton TypeTransfer;
	
	private DatabaseReference mDatabase;
	private FirebaseUser user;
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_moneyflow);
		
		user = FirebaseAuth.getInstance().getCurrentUser();
		
		// 유저 정보 가져오기
		// 유저가 없는 경우 error 발생
		user = FirebaseAuth.getInstance().getCurrentUser();
		if (user == null) {
			throw new IllegalArgumentException("로그인한 유저가 아닙니다.");
		}
		
		// 데이터베이스 연결
		mDatabase = FirebaseDatabase.getInstance().getReference();
		
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
		spinnerAccount = findViewById(R.id.input_moneyflow_accountspinner);
		accountAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
		accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAccount.setAdapter(accountAdapter);
		
		//분류 스피너
		spinnerCategory = findViewById(R.id.input_moneyflow_categoryspinner);
		categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ApplicationClass.mfExpenseCategoryList);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCategory.setAdapter(categoryAdapter);
		
		// 지출 Type 기본설정
		setType = "지출";
		// Type 버튼 연결
		TypeIncome = findViewById(R.id.input_moneyflow_type_income);
		TypeExpense = findViewById(R.id.input_moneyflow_type_expense);
		TypeTransfer = findViewById(R.id.input_moneyflow_type_Transfer);
		
		// 버튼 색 및 글씨 색 변경
		TypeExpense.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
		TypeExpense.setTextColor(ContextCompat.getColorStateList(this, R.color.colorBackground));
		
		
		// Type 선택시 몇몇 변수를 바꿔주는 로직 설정
		// 분류 스피너를 바꿔주면 됨
		// 이체의 경우 분류 스피너를 입금계좌로 바꿈
		TypeIncome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println(setDate.getTime());
				setType = "수입";
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("계좌");
				inputText.setText("분류");
				
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfIncomeCategoryList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerCategory.setAdapter(categoryAdapter);
				
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
				System.out.println(setDate.getTime());
				
				setType = "지출";
				
				TextView outputText = findViewById(R.id.input_moneyflow_accounttitle);
				TextView inputText = findViewById(R.id.input_moneyflow_categorytitle);
				outputText.setText("계좌");
				inputText.setText("분류");
				
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfExpenseCategoryList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerCategory.setAdapter(categoryAdapter);
				
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
				System.out.println(setDate.getTime());
				setType = "이체";
				//분류 스피너
				categoryAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_spinner_item, ApplicationClass.mfAccountList);
				categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerCategory.setAdapter(categoryAdapter);
				
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
		
		// 저장하기
		findViewById(R.id.input_moneyflow_save).setOnClickListener(new View.OnClickListener() {
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
				else if (setType.equals("이체") && spinnerAccount.getSelectedItem().toString().equals(spinnerCategory.getSelectedItem().toString())) {
					Toast.makeText(InputMoneyFlowCreateFB.this, "같은 계좌로 이체할 수 없습니다", Toast.LENGTH_SHORT).show();
					return;
				}
				// 문제가 없을 경우 Save
				else {
					// Save 가 여러번 되는 것을 방지하기 위해 버튼 비활성화 및 로딩 바 생성
					findViewById(R.id.input_moneyflow_save).setEnabled(false);
					findViewById(R.id.input_moneyflow_progresslayout).setVisibility(View.VISIBLE);
					
					// 유저 상태 확인 후
					if(user == null) {
						Toast.makeText(InputMoneyFlowCreateFB.this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
					}else {
						// 데이터 저장 장소에 연결
						mDatabase.child("moneyflow").child(user.getUid()).addListenerForSingleValueEvent(
								new ValueEventListener() {
									@Override
									public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
										// 데이터 집어넣는 메소드 실행
										writeNewMFData(Integer.parseInt(inputPrice.getText().toString().trim()),
												inputContent.getText().toString());
										// 비활성화 된 버튼 살리기
										findViewById(R.id.input_moneyflow_save).setEnabled(true);
										findViewById(R.id.input_moneyflow_progresslayout).setVisibility(View.GONE);
										// 창 닫기
										finish();
									}
									
									@Override
									public void onCancelled(@NonNull DatabaseError databaseError) {
									
									}
								}
						);
					}
					
					
				}
			}
		});
	}
	
	private void writeNewMFData(int price, String usage) {
		// key 는 새로운 키 생성
		String key = mDatabase.child("moneyflow").child(user.getUid()).push().getKey();
		// 데이터베이스에 데이터 목록을 추가
		//데이터가 들어갈 순서 : String type, Long date, String account, String category, Long price, String usage
		if (usage.length() <= 0) {
			usage = spinnerCategory.getSelectedItem().toString();
		}
		DataMoneyFlowFB newData = new DataMoneyFlowFB(setType,
				setDate.getTime(),
				spinnerAccount.getSelectedItem().toString(),
				spinnerCategory.getSelectedItem().toString(),
				(long) (int) price,
				usage);
		// JSON 에 전달할 수 있는 유형의 Map 만들기
		Map<String, Object> InputValues = newData.toMap();
		
		Map<String, Object> childUpdates = new HashMap<>();
		
		childUpdates.put("/moneyflow/" + user.getUid() + "/" + key, InputValues);
		// HashMap 데이터베이스에 집어넣기
		mDatabase.updateChildren(childUpdates);
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
				//TODO : 설정 창 만들어서 분류 변경 가능하도록 허용
				Toast.makeText(this, "설정 창 이동", Toast.LENGTH_SHORT).show();
			default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}*/
	
	// 액션 바 뒤로가기 버튼을 눌렀을 시 바로 종료
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return super.onSupportNavigateUp();
	}
}
