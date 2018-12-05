package com.blogspot.teperi31.moneydiary;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

// 처음 접속시 보일 화면 선택
public class MainTestActivity extends AppCompatActivity {
	// 로그인 데이터 및 전체 통계 데이터 가져오기
	private FirebaseUser mUser;
	private DatabaseReference mDatabaseReference;
	
	// 대시보드 차트 변수
	private CombinedChart chart;
	private final int count = 30;
	
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
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_test);
		
		//데이터 연결
		mUser = FirebaseAuth.getInstance().getCurrentUser();
		mDatabaseReference = FirebaseDatabase.getInstance().getReference();
		
		//캘린더 날짜 초기화
		mCalendarNow = Calendar.getInstance(Locale.KOREA);
		mCalendarFormerEnd = Calendar.getInstance(Locale.KOREA);
		mCalendarFormerStart = Calendar.getInstance(Locale.KOREA);
		
		//현재 월 받기
		mDateNow = new Date();
		mCalendarNow.setTime(mDateNow);
		
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		
		
		// 한달 전 시작 및 끝 날짜 받아오기
		mCalendarFormerStart.set(mCalendarNow.get(Calendar.YEAR),mCalendarNow.get(mCalendarNow.MONTH)-1,1);
		mDateFormerStart = mCalendarFormerStart.getTime();
		
		mCalendarFormerEnd.set(mCalendarNow.get(Calendar.YEAR),mCalendarNow.get(mCalendarNow.MONTH),1);
		mDateFormerEnd = mCalendarFormerEnd.getTime();
		
		
		chart = findViewById(R.id.activity_main_CombinedChart);
		
		chart.getDescription().setEnabled(false);
//		// 전체 배경색
//		chart.setBackgroundColor(R.color.colorPrimary);
		// 차트 내 배경색
		chart.setDrawGridBackground(false);
		// 차트 내 각각의 데이터의 배경색
		chart.setDrawBarShadow(false);
		// 무슨역할인지.. 차이 없으므로 제외
//		chart.setHighlightFullBarEnabled(true);
		
		
		// 차트 종류 선택
		// 라인 차트와 바 차트 모양 선택
		chart.setDrawOrder(new CombinedChart.DrawOrder[]{
				CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
		});
		
		// 범주 세팅하는 곳
		Legend l = chart.getLegend();
		// l.setWordWrapEnabled(false);
		// 위치 선정
		l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
		l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
		// 방향 선정
		l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
		
		// Y 좌표 삭제
		// Combined 의 경우 양쪽 Y 좌표가 나오기 때문에 둘 다 삭제 필요함
		YAxis yAxis = chart.getAxisLeft();
		yAxis.setAxisMinimum(0f); // start at zero
		chart.getAxisLeft().setEnabled(false);
		chart.getAxisRight().setEnabled(false);
		
		// X좌표 보이기
		XAxis xAxis = chart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setAxisMinimum(0f);
//		xAxis.setGranularity(1f);
		xAxis.setAxisMaximum(30f);
		
		CombinedData data = new CombinedData();
		
		data.notifyDataChanged();
		
		final ArrayList<BarEntry> entries1 = new ArrayList<>();
		Log.d("test","start");
		
		for(int i = 0; i < 31; i++) {
			entries1.add(new BarEntry(i+0.5f, 0f));
		}
		
		mDatabaseReference.child("moneyflow").child(mUser.getUid())
				.orderByChild("date").startAt(mDateFormerStart.getTime(),"date").endAt(mDateFormerEnd.getTime(),"date")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd");
						for(DataSnapshot item : dataSnapshot.getChildren()){
							DataMoneyFlowFB data = item.getValue(DataMoneyFlowFB.class);
							for(int i = Integer.parseInt(sdf.format(data.date)); i<31; i++){
								entries1.get(i).setY(entries1.get(i).getY() + data.price);
							}
						}
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
					
					}
					
				});
		
		
		BarDataSet set1 = new BarDataSet(entries1, "지출액");
		set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//		set1.setColor(Color.rgb(60, 220, 78));
//		set1.setValueTextColor(Color.rgb(60, 220, 78));
//		set1.setValueTextSize(12f);
//		set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//		set1.setDrawValues(false);
		
		BarData d = new BarData(set1);
		
		Log.d("test","here");
		
		
		data.setData(d);
		data.setData(generateLineData());
		
		
		
		chart.setData(data);
//		yAxis.setAxisMaximum(entries1.get(25).getY());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		chart.notifyDataSetChanged();
		chart.invalidate();
	}
	
	private LineData generateLineData() {
		
		
		
		
		
		LineData d = new LineData();
		
		final ArrayList<Entry> entries = new ArrayList<>();
		
		for(int i = 0; i < 31; i++) {
			entries.add(new BarEntry(i+0.5f, 0f));
		}
		
		
		mDatabaseReference.child("moneyflow").child(mUser.getUid())
				.orderByChild("date").startAt(mDateFormerEnd.getTime(),"date")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd");
						for(DataSnapshot item : dataSnapshot.getChildren()){
							DataMoneyFlowFB data = item.getValue(DataMoneyFlowFB.class);
							for(int i = Integer.parseInt(sdf.format(data.date)); i<31; i++){
								entries.get(i).setY(entries.get(i).getY() + data.price);
							}
						}
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
					
					}
					
				});
		
		LineDataSet set = new LineDataSet(entries, "Line DataSet");
		set.setColor(Color.rgb(240, 238, 70));
		set.setLineWidth(2.5f);
		set.setCircleColor(Color.rgb(240, 238, 70));
		set.setCircleRadius(5f);
//		set.setFillColor(getColor(R.color.colorAccent));
		set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//		set.setDrawValues(true);
//		set.setValueTextSize(10f);
//		set.setValueTextColor(Color.rgb(240, 238, 70));
		
		set.setDrawValues(false);
		
		set.setAxisDependency(YAxis.AxisDependency.LEFT);
		d.addDataSet(set);
		
		return d;
	}
	
	
	
	protected float getRandom(float range, float startsfrom) {
		return (float) (Math.random() * range) + startsfrom;
	}
}
