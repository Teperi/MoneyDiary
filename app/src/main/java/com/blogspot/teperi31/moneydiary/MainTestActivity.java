package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/*
 * 12.05 현재 상황
 * 그래프는 처음에 뜨지만 데이터가 0 형태로 되어있다가 갑자기 값이 전달되는 형태임
 * 이 문제를 해결하기 위해서 데이터를 기본으로 만들었다가 변경하는 형태로 접근해야 한다고 생각됨
 * 핸들러나 AsyncTask 를 통해 데이터를 받아오는 로딩을 걸어서 데이터를 받아오도록 함.
 * 받아온 데이터로 그래프를 다시 그린 후 이후 데이터를 가지고 그래프를 그리는 형식의 변경방식을 접근해 보도록 함
 *
 * 이후 작업 : 네비게이션바 만들어서 메인에 삽입할 수 있도록 작업해야함.
 * 네비게이션 바는 모든 페이지에 들어가야 함
 * 네비게이션 바는 아래쪽, 크기는 현재 액션바 사이즈와 같게 하면 됨.
 * 모든 xml 과 데이터를 건드려야 함.
 * */

// 처음 접속시 보일 화면 선택
public class MainTestActivity extends AppCompatActivity implements View.OnClickListener {
	// 로그인 데이터 및 전체 통계 데이터 가져오기
	private FirebaseUser mUser;
	private DatabaseReference mDatabaseReference;
	//Fcm 연결을 위한 토큰 처리
	UtilMyFirebaseMessagingService mFCM;
	
	// 날짜 확인 변수
	// 현재 날짜
	Date mDateNow;
	Calendar mCalendarNow;
	//월 초 날짜 확인
	Calendar mCalendarMonthStart;
	
	// 분류 설정 가져오기
	List<String> accountList;
	List<String> expenseCategoryList;
	List<String> incomeCategoryList;
	
	HashMap<String,Integer> expenseCategoryHashmap = new HashMap<>();
	
	// 파이 차트 연결 - 소비 리포트 용
	PieChart mExpenseChart;
	// 파이 차트 데이터 - 소비 리포트 용
	ArrayList<PieEntry> pieExpenselist = new ArrayList<>();
	PieDataSet pieExpenseDataSet;
	PieData pieExpenseData;
	
	// 바 차트 연결 - 입출금 리포트 용
	HorizontalBarChart mBalanceChart;
	// 바 차트 데이터 - 입출금 리포트 용
	ArrayList<BarEntry> barBalancelist = new ArrayList<>();
	BarDataSet barBalanceDataSet;
	BarData barBalanceData;
	
	ArrayList<IBarDataSet> barBalancelistValue = new ArrayList<>();
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_test);
		
		findViewById(R.id.activity_main_progressbar).setVisibility(View.VISIBLE);
		findViewById(R.id.activity_main_ScrollView).setVisibility(View.GONE);
		// 툴바 연결
		Toolbar mToolbar = findViewById(R.id.activity_main_toolbarTop);
		mToolbar.setTitle("DashBoard");
		setSupportActionBar(mToolbar);
		
		// 네비게이션 바 연결 및 색 설정
		findViewById(R.id.activity_main_bottomBar_listicon).setOnClickListener(this);
		findViewById(R.id.activity_main_bottomBar_messengericon).setOnClickListener(this);
		findViewById(R.id.activity_main_bottomBar_myinfoicon).setOnClickListener(this);
		
		//데이터 연결
		mUser = FirebaseAuth.getInstance().getCurrentUser();
		mDatabaseReference = FirebaseDatabase.getInstance().getReference();
		
		// Get token
		FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
			@Override
			public void onSuccess(InstanceIdResult instanceIdResult) {
				String deviceToken = instanceIdResult.getToken();
				mFCM = new UtilMyFirebaseMessagingService();
				mFCM.onNewToken(deviceToken);
			}
		});
		
		//캘린더 날짜 초기화
		mCalendarNow = Calendar.getInstance(Locale.KOREA);
		mCalendarMonthStart = Calendar.getInstance(Locale.KOREA);
		
		//현재 월 받기
		mDateNow = new Date();
		mCalendarNow.setTime(mDateNow);
		mCalendarMonthStart.set(mCalendarNow.get(Calendar.YEAR), mCalendarNow.get(Calendar.MONTH),0);
		Log.d("test", String.valueOf(mCalendarMonthStart.getTimeInMillis()));
		// 현재 날짜 소비 리포트에 넣기
		TextView dateExpensetext = findViewById(R.id.activity_main_expense_dateText);
		TextView dateBalancetext = findViewById(R.id.activity_main_balance_dateText);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM", Locale.KOREA);
		dateExpensetext.setText(sdf.format(mDateNow));
		dateBalancetext.setText(sdf.format(mDateNow));
		
		
		mExpenseChart = findViewById(R.id.activity_main_expense_pieChart);
		mBalanceChart = findViewById(R.id.activity_main_balance_barChart);
		// 소비 리포트 차트 세팅
		// 퍼센트 값 사용하기
		mExpenseChart.setUsePercentValues(true);
		// 배경 색 지정
		mExpenseChart.setTransparentCircleColor(Color.WHITE);
		// 가운데 빈 공간 크기 지정
		mExpenseChart.setHoleRadius(58f);
		// 가운데 반투명 공간 크기 지정
		mExpenseChart.setTransparentCircleRadius(62f);
		// 범주 및 아래 설명 텍스트 지우기
		mExpenseChart.getLegend().setEnabled(false);   // Hide the legend
		mExpenseChart.getDescription().setEnabled(false);
		
		// 입출금 리포트 차트 세팅
		// 범주 및 아래 설명 텍스트 지우기
		mBalanceChart.getLegend().setEnabled(false);
		mBalanceChart.getDescription().setEnabled(false);
		
		// 눈금이 있는 값 모두 지우기
		mBalanceChart.getXAxis().setEnabled(false);
		mBalanceChart.getAxisLeft().setEnabled(false);
		mBalanceChart.getAxisRight().setEnabled(false);
		
		// 분류 데이터 및 리포트할 값 가져오기
		mDatabaseReference.child("users-setting").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				// 분류 데이터 우선 가져오기
				expenseCategoryList = (List<String>) dataSnapshot.child("expenseCategoryList").getValue();
				for(int i = 0; i < expenseCategoryList.size(); i++) {
					expenseCategoryHashmap.put(expenseCategoryList.get(i),0);
				}
				// 최근 월을 찾아서 그 데이터만 가져오기
				// 지출 데이터 및 입출금 데이터 같이 만들기
				mDatabaseReference.child("moneyflow").child(mUser.getUid()).orderByChild("date")
						.startAt(mCalendarMonthStart.getTimeInMillis())
						.addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
								// 데이터가 있으면
								if(dataSnapshot.exists()){
									// 데이터 없다는 글씨 지우기
									findViewById(R.id.activity_main_expense_noDataText).setVisibility(View.GONE);
									// 날짜에 맞는 전체 데이터 중
									int incometotal = 0;
									int expensetotal = 0;
									for(DataSnapshot item : dataSnapshot.getChildren()) {
										DataMoneyFlowFB test = item.getValue(DataMoneyFlowFB.class);
										// 실제 데이터 넣기
										for(int i = 0; i < expenseCategoryList.size(); i++){
											if(test.category.equals(expenseCategoryList.get(i)) && test.type.equals("지출")){
												expenseCategoryHashmap.put(expenseCategoryList.get(i),expenseCategoryHashmap.get(expenseCategoryList.get(i)) + Math.toIntExact(test.price));
												expensetotal += Math.toIntExact(test.price);
											} else if(test.type.equals("수입")) {
												incometotal += Math.toIntExact(test.price);
											}
										}
									}
									// 입출금 리포트 차트 넣기
									barBalancelist.add(new BarEntry(0f,incometotal));
									barBalancelist.add(new BarEntry(1f,expensetotal));
									Log.i("test", "수입 : " + String.valueOf(incometotal));
									Log.i("test", "지출 : " + String.valueOf(expensetotal));
									// 실제에서 뽑은 데이터를 차트에 넣기
									for(int i = 0; i<expenseCategoryHashmap.size();i++){
										if(expenseCategoryHashmap.get(expenseCategoryList.get(i)) > 0){
											pieExpenselist.add(new PieEntry(expenseCategoryHashmap.get(expenseCategoryList.get(i)),expenseCategoryList.get(i)));
										}
									}
									// 지출 리포트 데이터 집어넣기
									pieExpenseDataSet = new PieDataSet(pieExpenselist, "지출 리포트");
									pieExpenseDataSet.setSliceSpace(3f);
									pieExpenseDataSet.setSelectionShift(5f);
									
									pieExpenseDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
									
									pieExpenseData = new PieData(pieExpenseDataSet);
									pieExpenseData.setValueFormatter(new PercentFormatter());
									pieExpenseData.setValueTextSize(15f);
									pieExpenseData.setValueTextColor(Color.WHITE);
									
									// 값 글씨 크기 지정
									mExpenseChart.setEntryLabelTextSize(15f);
									
									// 총 소비액 가운데에 쓰기
									mExpenseChart.setCenterTextSize(20f);
									mExpenseChart.setCenterText("총 소비액 :" + toNumFormat(expensetotal));
									
									// 입출금 리포트 데이터 집어넣기
									barBalanceDataSet = new BarDataSet(barBalancelist, "입출금 리포트");
									barBalanceDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
									barBalanceDataSet.setDrawValues(true);
									
									
									barBalanceData = new BarData(barBalanceDataSet);
									
									
									findViewById(R.id.activity_main_progressbar).setVisibility(View.GONE);
									findViewById(R.id.activity_main_ScrollView).setVisibility(View.VISIBLE);
									findViewById(R.id.activity_main_balance_card).setVisibility(View.GONE);
									mExpenseChart.setVisibility(View.VISIBLE);
									mBalanceChart.setVisibility(View.VISIBLE);
									findViewById(R.id.activity_main_expense_noDataText).setVisibility(View.GONE);
									findViewById(R.id.activity_main_balance_noDataText).setVisibility(View.GONE);
									
									mBalanceChart.animateY(1400);
									mExpenseChart.animateY(1400, Easing.EaseInOutQuad);
									mBalanceChart.setData(barBalanceData);
									mExpenseChart.setData(pieExpenseData);
									
								} else {
									findViewById(R.id.activity_main_balance_card).setVisibility(View.GONE);
									// 데이터가 없으면 없다고 띄우기
									findViewById(R.id.activity_main_ScrollView).setVisibility(View.VISIBLE);
									findViewById(R.id.activity_main_expense_noDataText).setVisibility(View.VISIBLE);
									findViewById(R.id.activity_main_balance_noDataText).setVisibility(View.VISIBLE);
									mExpenseChart.setVisibility(View.GONE);
									mBalanceChart.setVisibility(View.GONE);
									findViewById(R.id.activity_main_progressbar).setVisibility(View.GONE);
								}
								
							}
							
							@Override
							public void onCancelled(@NonNull DatabaseError databaseError) {
							
							}
						});
				
				
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		((ImageButton) findViewById(R.id.activity_main_bottomBar_dashboardicon)).setImageResource(R.drawable.ic_action_dashboard_clicked);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.activity_main_bottomBar_listicon:
				startActivity(new Intent(this, RecyclerViewMoneyFlowFB.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			case R.id.activity_main_bottomBar_messengericon:
				startActivity(new Intent(this, MessengerChatRoomList.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			case R.id.activity_main_bottomBar_myinfoicon:
				startActivity(new Intent(this, SignInAccountInfo.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			default:
				break;
		}
	}
	
	//	toolbar 에 메뉴 띄워주는 함수
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	
	//	메뉴 버튼 클릭시 나올 상황 입력
	//  일단 toast만 연결함 / 추후 Intent 기능 넣어야 함
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
			case R.id.main_menu_AppInfo:
				startActivity(new Intent(this, AppInfo.class));
				overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
				return true;
			
//			case R.id.main_menu_AppSetting:
//				Toast.makeText(this, "환경 설정", Toast.LENGTH_SHORT).show();
//				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public static String toNumFormat(int num) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num);
	}
	
}
