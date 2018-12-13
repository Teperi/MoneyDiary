package com.blogspot.teperi31.moneydiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerImage;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	
	// 대시보드 차트
	private CombinedChart mCombinedChart;
	
	// 차트 데이터를 최종적으로 담는 그릇
	private CombinedData mCombinedData;
	
	// 전체 날짜 고정
	private final int count = 31;
	
	
	// 날짜 확인 변수
	// 현재 날짜
	Date mDateNow;
	Calendar mCalendarNow;
	
	// 한달전 시작 날짜
	Date mDateFormerStart;
	Calendar mCalendarFormerStart;
	// 한달전 끝 날짜
	Date mDateFormerEnd;
	Calendar mCalendarFormerEnd;
	
	// 바차트와 라인차트 데이터를 Arraylist 에 우선 담아야 함
	ArrayList<BarEntry> barEntries;
	ArrayList<Entry> lineEntries;
	// Arraylist 에 담은 데이터를 데이터셋으로 저장
	BarDataSet mBarDataSet;
	LineDataSet mlineDataSet;
	// 데이터셋을 만든후 세팅까지 하고 나서 -> 데이터로 저장해야 함
	BarData mBarData;
	LineData mlineData;
	MarkerImage mMarkerImage;
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_test);
		findViewById(R.id.activity_main_progressbar).setVisibility(View.VISIBLE);
		findViewById(R.id.activity_main_ScrollView).setVisibility(View.GONE);
		// 툴바 연결
		Toolbar mToolbar = findViewById(R.id.activity_main_toolbarTop);
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
		mCalendarFormerEnd = Calendar.getInstance(Locale.KOREA);
		mCalendarFormerStart = Calendar.getInstance(Locale.KOREA);
		
		//현재 월 받기
		mDateNow = new Date();
		mCalendarNow.setTime(mDateNow);
		
		// 현재 날짜 소비 리포트에 넣기
		TextView datetext = findViewById(R.id.activity_main_expense_dateText);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
		datetext.setText(sdf.format(mDateNow));
		
		
		// 한달 전 시작 및 끝 날짜 받아오기
		mCalendarFormerStart.set(mCalendarNow.get(Calendar.YEAR), mCalendarNow.get(mCalendarNow.MONTH) - 1, 1);
		mDateFormerStart = mCalendarFormerStart.getTime();
		mCalendarFormerEnd.set(mCalendarNow.get(Calendar.YEAR), mCalendarNow.get(mCalendarNow.MONTH), 1);
		mDateFormerEnd = mCalendarFormerEnd.getTime();
		
		// 대시보
		mCombinedChart = findViewById(R.id.activity_main_expense_CombinedChart);
		
		mCombinedChart.getDescription().setEnabled(false);
//		// 전체 배경색
//		mCombinedChart.setBackgroundColor(R.color.colorPrimary);
		// 차트 내 배경색
		mCombinedChart.setDrawGridBackground(false);
		// 차트 내 각각의 데이터의 배경색
		mCombinedChart.setDrawBarShadow(false);
		// 무슨역할인지.. 차이 없으므로 제외
//		mCombinedChart.setHighlightFullBarEnabled(true);
		
		
		// 차트 종류 선택
		// 라인 차트와 바 차트 모양 선택
		mCombinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
				CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
		});
		
		// 범주 세팅하는 곳
		Legend l = mCombinedChart.getLegend();
		// l.setWordWrapEnabled(false);
		// 위치 선정
		l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
		l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
		// 방향 선정
		l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
		
		// Y 좌표 삭제
		// Combined 의 경우 양쪽 Y 좌표가 나오기 때문에 둘 다 삭제 필요함
		YAxis yAxis = mCombinedChart.getAxisLeft();
		yAxis.setAxisMinimum(0f); // start at zero
		mCombinedChart.getAxisLeft().setEnabled(false);
		mCombinedChart.getAxisRight().setEnabled(false);
		mCombinedChart.setMarker(mMarkerImage);
		
		// X좌표 보이기
		XAxis xAxis = mCombinedChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setAxisMinimum(0f);
//		xAxis.setGranularity(1f);
		xAxis.setAxisMaximum(30f);
		
		mDatabaseReference.child("moneyflow").child(mUser.getUid())
				.orderByChild("date").startAt(mDateFormerStart.getTime(), "date")
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						if (dataSnapshot.exists()) {
							findViewById(R.id.activity_main_expense_noDataText).setVisibility(View.GONE);
							findViewById(R.id.activity_main_expense_CombinedChart).setVisibility(View.VISIBLE);
							SimpleDateFormat sdf = new SimpleDateFormat("dd");
							// 바 데이터 새로 만들기
							barEntries = new ArrayList<>();
							for (int i = 0; i < 31; i++) {
								barEntries.add(new BarEntry(i + 0.5f, 0f));
							}
							// 라인 데이터 새로 만들기
							lineEntries = new ArrayList<>();
							for (int i = 0; i < Integer.parseInt(sdf.format(mDateNow)); i++) {
								if (i == 4 || i == 9 || i == 14 || i == 19 || i == 24 || i == 29) {
									lineEntries.add(new Entry(i + 0.5f, 0f));
									lineEntries.get(i).setIcon(getDrawable(R.drawable.ic_action_charticon));
								} else {
									lineEntries.add(new Entry(i + 0.5f, 0f));
								}
							}
							
							for (DataSnapshot item : dataSnapshot.getChildren()) {
								DataMoneyFlowFB DMFdata = item.getValue(DataMoneyFlowFB.class);
								if (DMFdata.date < mDateFormerEnd.getTime()) {
									for (int i = Integer.parseInt(sdf.format(DMFdata.date)); i < 31; i++) {
										barEntries.get(i).setY(barEntries.get(i).getY() + DMFdata.price);
									}
								} else {
									for (int i = Integer.parseInt(sdf.format(DMFdata.date)); i < Integer.parseInt(sdf.format(mDateNow)); i++) {
										if (i == 4 || i == 9 || i == 14 || i == 19 || i == 24 || i == 29) {
											lineEntries.get(i).setY(lineEntries.get(i).getY() + DMFdata.price);
//											lineEntries.get(i).setIcon(getDrawable(R.drawable.ic_action_charticon));
										} else {
											lineEntries.get(i).setY(lineEntries.get(i).getY() + DMFdata.price);
										}
									}
								}
							}
							Log.d("test", "여긴언제?");
							
							// 바데이터 셋 세팅 -> 바데이터로 저장
							mBarDataSet = new BarDataSet(barEntries, String.valueOf(mCalendarFormerStart.get(Calendar.MONTH) + 1) + "월");
							mBarDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
							mBarDataSet.setDrawValues(false);
							mBarDataSet.setColor(getColor(R.color.colorGrayline));
							mBarData = new BarData(mBarDataSet);
							
							// 라인데이터 셋 및 세팅 -> 라인데이터로 저장
							mlineDataSet = new LineDataSet(lineEntries, String.valueOf(mCalendarNow.get(Calendar.MONTH) + 1) + "월");
							mlineDataSet.setColor(getColor(R.color.colorBlack));
							mlineDataSet.setLineWidth(1.5f);
							mlineDataSet.setMode(LineDataSet.Mode.LINEAR);
							mlineDataSet.setDrawValues(false);
							mlineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
							mlineDataSet.setDrawCircles(false);
							mlineData = new LineData(mlineDataSet);
							
							// 데이터들을 차트 내 데이터로 넣기
							mCombinedData = new CombinedData();
							mCombinedData.setData(mBarData);
							mCombinedData.setData(mlineData);
							
							// 차트에 그리기
							mCombinedChart.setData(mCombinedData);
							findViewById(R.id.activity_main_ScrollView).setVisibility(View.VISIBLE);
							findViewById(R.id.activity_main_progressbar).setVisibility(View.GONE);
						} else {
							findViewById(R.id.activity_main_ScrollView).setVisibility(View.VISIBLE);
							findViewById(R.id.activity_main_progressbar).setVisibility(View.GONE);
							findViewById(R.id.activity_main_expense_noDataText).setVisibility(View.VISIBLE);
							findViewById(R.id.activity_main_expense_CombinedChart).setVisibility(View.GONE);
						}
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
				Toast.makeText(this, "앱 소개", Toast.LENGTH_SHORT).show();
				return true;
			
			case R.id.main_menu_AppSetting:
				Toast.makeText(this, "환경 설정", Toast.LENGTH_SHORT).show();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
