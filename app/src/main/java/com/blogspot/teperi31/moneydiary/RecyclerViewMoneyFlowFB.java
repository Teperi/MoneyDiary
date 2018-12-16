package com.blogspot.teperi31.moneydiary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
 * 파이어베이스와 연동한 리사이클러뷰 만들기
 *
 * */
public class RecyclerViewMoneyFlowFB extends AppCompatActivity implements View.OnClickListener {
	
	Toolbar mToolbar;
	
	private DatabaseReference mDatabase;
	private FirebaseUser user;
	
	private AdapterMoneyFlowList mAdapter;
	private RecyclerView mRecycler;
	private LinearLayoutManager mLayoutManager;
	
	// 액션모드 선택을 위한 저장 데이터
	private Map<String, String> selectedItems = new HashMap<>();
	
	private Boolean filterSwitch = false;
	
	Query MoneyFlowQuery;
	// 날짜 필터를 위한 날짜 받아오기
	Date dateNow;
	Calendar calendarNow;
	Calendar filterDateStartCalendar;
	Calendar filterDateEndCalendar;
	// typeint 1 : 전체 보이기, 2 : 지출만 보이기, 3 : 수입만 보이기
	int TypeRadioSelect = 1;
	
	/*
	 * 뷰 생성 시
	 * 액션바 생성
	 * 데이터베이스 연결
	 * 리사이클러뷰 연결 & 사이즈 고정
	 * 어댑터 만들기
	 * */
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moneyflow_list);
		
		findViewById(R.id.moneyflow_list_fab).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RecyclerViewMoneyFlowFB.this, InputMoneyFlowCreateFB.class);
				startActivity(intent);
//				Animation animation = AnimationUtils.loadAnimation(RecyclerViewMoneyFlowFB.this, R.anim.scale_up);
//				animation.start();
			}
		});
		
		
		// 액션바
		mToolbar = findViewById(R.id.moneyflow_list_toolbarTop);
		mToolbar.setTitle("가계부 리스트");
		setSupportActionBar(mToolbar);
		
		
		// 네비게이션바
		findViewById(R.id.moneyflow_list_bottomBar_dashboardicon).setOnClickListener(this);
		findViewById(R.id.moneyflow_list_bottomBar_messengericon).setOnClickListener(this);
		findViewById(R.id.moneyflow_list_bottomBar_myinfoicon).setOnClickListener(this);
		((ImageButton) findViewById(R.id.moneyflow_list_bottomBar_listicon)).setImageResource(R.drawable.ic_action_list_clicked);
		
		
		// DB 연결
		mDatabase = FirebaseDatabase.getInstance().getReference();
		mDatabase.keepSynced(true);
		user = FirebaseAuth.getInstance().getCurrentUser();
		// 리사이클러뷰 연결 & 고정
		mRecycler = findViewById(R.id.moneyflow_list_recyclerview);
		mRecycler.setHasFixedSize(true);
		
		// 세로로 쌓기 기능
		mLayoutManager = new LinearLayoutManager(this);
		// Query 에 있는 정렬 기능을 역순으로 해 주는 부분
		mLayoutManager.setReverseLayout(true);
		mLayoutManager.setStackFromEnd(true);
		
		mRecycler.setLayoutManager(mLayoutManager);
		
		// 가져올 데이터 쿼리 정렬 - 날짜 데이터 기준 정렬
		MoneyFlowQuery = mDatabase.child("moneyflow").child(user.getUid()).orderByChild("date");
		
		
		// 데이터 가져오는 builder 설정
		FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataMoneyFlowFB>()
				.setQuery(MoneyFlowQuery, DataMoneyFlowFB.class)
				.build();
		
		// 리사이클러 어뎁터 설정
		
		mAdapter = new AdapterMoneyFlowList(options, TypeRadioSelect);
		
		// 어뎁터와 리사이클러뷰 연결
		mRecycler.setAdapter(mAdapter);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mProgessStart();
		mDatabase.child("moneyflow").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Long incometotal = 0L;
				Long expensetotal = 0L;
				Long balancetotal = 0L;
				for (DataSnapshot item : dataSnapshot.getChildren()) {
					DataMoneyFlowFB data = item.getValue(DataMoneyFlowFB.class);
					if (data.type.equals("수입")) {
						incometotal += data.price;
					} else if (data.type.equals("지출")) {
						expensetotal += data.price;
					}
				}
				balancetotal = incometotal - expensetotal;
				
				((TextView) findViewById(R.id.moneyflow_list_incomeText)).setText(toNumFormat(incometotal));
				((TextView) findViewById(R.id.moneyflow_list_incomeText)).setTextColor(getColor(R.color.colorPrimaryDark));
				((TextView) findViewById(R.id.moneyflow_list_expenseText)).setText(toNumFormat(expensetotal));
				((TextView) findViewById(R.id.moneyflow_list_expenseText)).setTextColor(getColor(R.color.colorError));
				((TextView) findViewById(R.id.moneyflow_list_balanceText)).setText(toNumFormat(balancetotal));
				if (balancetotal <= 0) {
					((TextView) findViewById(R.id.moneyflow_list_balanceText)).setTextColor(getColor(R.color.colorError));
				} else {
					((TextView) findViewById(R.id.moneyflow_list_balanceText)).setTextColor(getColor(R.color.colorPrimaryDark));
				}
				
				
				// 어뎁터가 있으면 실시간 연결
				if (mAdapter != null) {
					mAdapter.startListening();
					mProgessStop();
				} else {
					mProgessStop();
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// 페이지가 멈출 때 실시간 연결기능 해제
		if (mAdapter != null) {
			mAdapter.stopListening();
		}
	}
	
	
	// 로딩 모양 보여주는 메소드
	private void mProgessStart() {
		findViewById(R.id.moneyflowcalc).setVisibility(View.GONE);
		findViewById(R.id.moneyflow_list_recyclerview).setVisibility(View.GONE);
		findViewById(R.id.moneyflow_recycler_progress).setVisibility(View.VISIBLE);
	}
	
	// 로딩이 끝난 경우 로딩 모양 없애주는
	public void mProgessStop() {
		findViewById(R.id.moneyflow_list_recyclerview).setVisibility(View.VISIBLE);
		findViewById(R.id.moneyflow_recycler_progress).setVisibility(View.GONE);
		findViewById(R.id.moneyflowcalc).setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//네비게이션 버튼 클릭시 이동하는 인텐트
			case R.id.moneyflow_list_bottomBar_dashboardicon:
				startActivity(new Intent(this, MainTestActivity.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				break;
			case R.id.moneyflow_list_bottomBar_messengericon:
				startActivity(new Intent(this, MessengerChatRoomList.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			case R.id.moneyflow_list_bottomBar_myinfoicon:
				startActivity(new Intent(this, SignInAccountInfo.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			default:
				break;
		}
	}

//	public boolean toggleSelection(String key) {
//		boolean b;
//		if (selectedItems.get(key)!=null) {
//			selectedItems.remove(key);
//			b = false;
//		}
//		else {
//			selectedItems.put(key, key);
//			b = true;
//		}
//		int n = getSelectedItemCount();
//		if (n>0 && actionMode!=null) {
//			actionMode.setTitle(String.valueOf(n) + (n==1 ? " selecionado" : " selecionados"));
//		}
//		return b;
//	}
//
//	private void removeItems() {
//		String userId = fragment.getUid();
//
//		Iterator iterator = selectedItems.keySet().iterator();
//		while(iterator.hasNext()) {
//			String key=(String) iterator.next();
//			fragment.mDatabase.child("user-news").child(userId).child(key).removeValue();
//		}
//		selectedItems.clear();
//	}
	
	public static String toNumFormat(Long num) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num);
	}
	
	
	//	toolbar 에 메뉴 띄워주는 함수
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.moneyflow_list_menu, menu);
		return true;
	}
	
	//	메뉴 버튼 클릭시 나올 상황 입력
	//  일단 toast만 연결함 / 추후 Intent 기능 넣어야 함
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
			case R.id.moneyflow_list_menu_filterlist:
				if (filterSwitch) {
					filterSwitch = false;
					findViewById(R.id.moneyflow_list_filterCardView).setVisibility(View.GONE);
					
					mProgessStart();
					mAdapter.stopListening();
					
					// 원래 데이터로 복귀
					// 가져올 데이터 쿼리 정렬 - 날짜 데이터 기준 정렬
					MoneyFlowQuery = mDatabase.child("moneyflow").child(user.getUid()).orderByChild("date");
					
					
					// 데이터 가져오는 builder 설정
					FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataMoneyFlowFB>()
							.setQuery(MoneyFlowQuery, DataMoneyFlowFB.class)
							.build();
					
					TypeRadioSelect = 1;
					// 리사이클러 어뎁터 설정
					mAdapter = new AdapterMoneyFlowList(options, TypeRadioSelect);
					
					// 어뎁터와 리사이클러뷰 연결
					mRecycler.setAdapter(mAdapter);
					mAdapter.startListening();
					mProgessStop();
				} else {
					// 필터 보이는 중 확인
					filterSwitch = true;
					// 리사이클러 노출 잠시 멈추기
					mAdapter.stopListening();
					//로딩바 시작
					mProgessStart();
					// 날짜 확인
					dateNow = new Date();
					calendarNow = Calendar.getInstance(Locale.KOREA);
					calendarNow.setTime(dateNow);
					// 월별 보기를 위한 날짜 가져오기
					filterDateStartCalendar = Calendar.getInstance(Locale.KOREA);
					filterDateEndCalendar = Calendar.getInstance(Locale.KOREA);
					filterDateStartCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH), 1);
					// 만약 12월인 경우 다음 해로 넘기기
					if (calendarNow.get(Calendar.MONTH) >= 11) {
						filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR) + 1, 0, 1);
					} else {
						filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH) + 1, 1);
					}
					// 데이터 다시 정렬해서 리사이클러뷰 뿌리기
					monthChangeQuery(filterDateStartCalendar, filterDateEndCalendar, TypeRadioSelect);
					
					// 1달 이전으로 가는 버튼을 눌렀을 경우
					findViewById(R.id.moneyflow_list_filter_dateBeforeButton).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// 만약 지금이 1월이라면 -> 년도를 1년 줄여야함
							if (calendarNow.get(Calendar.MONTH) == 0) {
								calendarNow.set(calendarNow.get(Calendar.YEAR) - 1, 11, calendarNow.get(Calendar.DAY_OF_MONTH));
								filterDateStartCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH), 1);
								// 만약 12월인 경우 다음 해로 넘기기
								if (calendarNow.get(Calendar.MONTH) >= 11) {
									filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR) + 1, 0, 1);
								} else {
									filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH) + 1, 1);
								}
								monthChangeQuery(filterDateStartCalendar, filterDateEndCalendar, 1);
							} else {
								calendarNow.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH) - 1, calendarNow.get(Calendar.DAY_OF_MONTH));
								filterDateStartCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH), 1);
								// 만약 12월인 경우 다음 해로 넘기기
								if (calendarNow.get(Calendar.MONTH) >= 11) {
									filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR) + 1, 0, 1);
								} else {
									filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH) + 1, 1);
								}
								monthChangeQuery(filterDateStartCalendar, filterDateEndCalendar, TypeRadioSelect);
							}
						}
					});
					
					// 1달 이후 보기 버튼을 눌렀을 경우
					findViewById(R.id.moneyflow_list_filter_dateAfterButton).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// 만약 지금이 12월 이라면 -> 내년으로 넘겨야 함
							if (calendarNow.get(Calendar.MONTH) == 11) {
								calendarNow.set(calendarNow.get(Calendar.YEAR) + 1, 0, calendarNow.get(Calendar.DAY_OF_MONTH));
								filterDateStartCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH), 1);
								// 만약 12월인 경우 다음 해로 넘기기
								if (calendarNow.get(Calendar.MONTH) >= 11) {
									filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR) + 1, 0, 1);
								} else {
									filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH) + 1, calendarNow.get(Calendar.DAY_OF_MONTH));
								}
								monthChangeQuery(filterDateStartCalendar, filterDateEndCalendar, 1);
							} else {
								calendarNow.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH) + 1, 1);
								filterDateStartCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH), 1);
								// 만약 12월인 경우 다음 해로 넘기기
								if (calendarNow.get(Calendar.MONTH) >= 11) {
									filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR) + 1, 0, 1);
								} else {
									filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH) + 1, 1);
								}
								monthChangeQuery(filterDateStartCalendar, filterDateEndCalendar, TypeRadioSelect);
							}
						}
					});
					
					// 만약 날짜 선택을 하기로 결정했다면
					findViewById(R.id.moneyflow_list_filter_dateText).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							DatePickerDialog.OnDateSetListener date;
							// 날짜를 변경해주는 값 받아주는 변수
							date = new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
									// 새로운 날짜 받아오기
									calendarNow.set(year, month, dayOfMonth);
									filterDateStartCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH), 1);
									// 만약 12월인 경우 다음 해로 넘기기
									if (calendarNow.get(Calendar.MONTH) >= 11) {
										filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR) + 1, 0, 1);
									} else {
										filterDateEndCalendar.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH) + 1, 1);
									}
									monthChangeQuery(filterDateStartCalendar, filterDateEndCalendar, TypeRadioSelect);
								}
							};
							new DatePickerDialog(v.getContext(), date, calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH), calendarNow.get(Calendar.DAY_OF_MONTH)).show();
							
						}
					});
					
					// 라디오버튼 그룹 설정
					RadioGroup typeGroup = findViewById(R.id.moneyflow_list_filter_typeRadioGroup);
					typeGroup.check(R.id.moneyflow_list_filter_typeTotalRadio);
					TypeRadioSelect = 1;
					typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(RadioGroup group, int checkedId) {
							switch (checkedId) {
								case R.id.moneyflow_list_filter_typeTotalRadio:
									TypeRadioSelect = 1;
									monthChangeQuery(filterDateStartCalendar, filterDateEndCalendar, TypeRadioSelect);
									break;
								case R.id.moneyflow_list_filter_typeExpenseRadio:
									TypeRadioSelect = 2;
									monthChangeQuery(filterDateStartCalendar, filterDateEndCalendar, TypeRadioSelect);
									break;
								case R.id.moneyflow_list_filter_typeIncomeRadio:
									TypeRadioSelect = 3;
									monthChangeQuery(filterDateStartCalendar, filterDateEndCalendar, TypeRadioSelect);
									break;
								default:
									break;
							}
						}
					});
					
					
				}
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void monthChangeQuery(Calendar StartCal, Calendar EndCal, int Typeint) {
		// 쿼리 변경
		MoneyFlowQuery = mDatabase.child("moneyflow").child(user.getUid())
				.orderByChild("date")
				.startAt(StartCal.getTimeInMillis())
				.endAt(EndCal.getTimeInMillis() - 60000L);
		// 쿼리 변경값 옵션에 재설정
		FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataMoneyFlowFB>()
				.setQuery(MoneyFlowQuery, DataMoneyFlowFB.class)
				.build();
		
		// 리사이클러 어뎁터 설정
		mAdapter = new AdapterMoneyFlowList(options, Typeint);
		mRecycler.setAdapter(mAdapter);
		
		// 수입 지출 잔액 값 변경
		mDatabase.child("moneyflow").child(user.getUid()).orderByChild("date")
				.startAt(StartCal.getTimeInMillis())
				.endAt(EndCal.getTimeInMillis() - 60000L)
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						Long incometotal = 0L;
						Long expensetotal = 0L;
						Long balancetotal = 0L;
						for (DataSnapshot item : dataSnapshot.getChildren()) {
							DataMoneyFlowFB data = item.getValue(DataMoneyFlowFB.class);
							if (data.type.equals("수입")) {
								incometotal += data.price;
							} else if (data.type.equals("지출")) {
								expensetotal += data.price;
							}
						}
						balancetotal = incometotal - expensetotal;
						
						((TextView) findViewById(R.id.moneyflow_list_incomeText)).setText(toNumFormat(incometotal));
						((TextView) findViewById(R.id.moneyflow_list_incomeText)).setTextColor(getColor(R.color.colorPrimaryDark));
						((TextView) findViewById(R.id.moneyflow_list_expenseText)).setText(toNumFormat(expensetotal));
						((TextView) findViewById(R.id.moneyflow_list_expenseText)).setTextColor(getColor(R.color.colorError));
						((TextView) findViewById(R.id.moneyflow_list_balanceText)).setText(toNumFormat(balancetotal));
						if (balancetotal <= 0) {
							((TextView) findViewById(R.id.moneyflow_list_balanceText)).setTextColor(getColor(R.color.colorError));
						} else {
							((TextView) findViewById(R.id.moneyflow_list_balanceText)).setTextColor(getColor(R.color.colorPrimaryDark));
						}
						
						// 어뎁터가 있으면 실시간 연결
						if (mAdapter != null) {
							mAdapter.startListening();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월", Locale.KOREA);
							((TextView) findViewById(R.id.moneyflow_list_filter_dateText)).setText(sdf.format(calendarNow.getTime()));
							
							findViewById(R.id.moneyflow_list_filterCardView).setVisibility(View.VISIBLE);
							
							mProgessStop();
						} else {
							mProgessStop();
						}
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
					
					}
				});
	}
	
	
}
