package com.blogspot.teperi31.moneydiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;


public class RecyclerviewMoneyFlow extends AppCompatActivity {
	
	// 전화 걸기 권한 허용 요청
	final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
	
	
	private RecyclerView mRecyclerView;
	private RecyclerView.LayoutManager mLayoutManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recyclerview_moneyflow);
		
		
		
		
		// 액션 바 삽입
		Toolbar myToolbar = findViewById(R.id.toolbarTop_List_moneyflow);
		setSupportActionBar(myToolbar);
		
		Collections.sort(ApplicationClass.mfList, new Comparator<DataMoneyFlow>() {
			@Override
			public int compare(DataMoneyFlow o1, DataMoneyFlow o2) {
				return o2.MFListDate.compareTo(o1.MFListDate);
			}
		});
		
		
		mRecyclerView = findViewById(R.id.moneyflow_recycler_view);
		
		// 사이즈 고정, 리사이클러 뷰에서 content 사이즈를 바꾸지 말라?
		mRecyclerView.setHasFixedSize(true);
		
		// 리니어 레이아웃 매니저. 한줄씩 쌓임
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		
		
		// 데이터 입력
		
		
		// 데이터가 입력되었을 때 저장
		Intent InputCreateSpendIntent = getIntent();
		
		// 새로운 지출 데이터가 들어오면 저장시킴
		
		
		
		
		//내 어댑터와 데이터 연결
		AdapterMFRecycler myAdapter = new AdapterMFRecycler(ApplicationClass.mfList);
		
		mRecyclerView.setAdapter(myAdapter);
		
		
		// 가격 전체 데이터 저장
		int moneyflowDepositint = 0;
		int moneyflowWithdrawint = 0;
		int moneyflowTotalint;
		TextView moneyflowDeposittext = findViewById(R.id.moneyflowcalcdeposit);
		TextView moneyflowWithdrawtext = findViewById(R.id.moneyflowcalcwithdraw);
		TextView moneyflowTotaltext = findViewById(R.id.moneyflowcalctotal);
		
		// 수입 및 지출 계산
		for (int i = 0; i < ApplicationClass.mfList.size(); i++) {
			if (ApplicationClass.mfList.get(i).MFListType.equals("입금")) {
				moneyflowDepositint += ApplicationClass.mfList.get(i).MFListPrice;
			} else if (ApplicationClass.mfList.get(i).MFListType.equals("출금")) {
				moneyflowWithdrawint += ApplicationClass.mfList.get(i).MFListPrice;
			}
		}
		
		// 총액 계산
		moneyflowTotalint = moneyflowDepositint - moneyflowWithdrawint;
		
		// 텍스트 출력
		moneyflowDeposittext.setText(String.valueOf(moneyflowDepositint));
		moneyflowWithdrawtext.setText(String.valueOf(moneyflowWithdrawint));
		moneyflowTotaltext.setText(String.valueOf(moneyflowTotalint));
		
		moneyflowDeposittext.setTextColor(Color.parseColor("#c62828"));
		moneyflowWithdrawtext.setTextColor(Color.parseColor("#1a237e"));


//
	}
	
	//	toolbar 에 메뉴 띄워주는 함수
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topbar_actions_moneyflow, menu);
		return true;
	}
	
	//	메뉴 버튼 클릭시 나올 상황 입력
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionAdd:
				Intent i = new Intent(RecyclerviewMoneyFlow.this, InputSpendCreate.class);
				startActivity(i);
				Toast.makeText(this, "추가", Toast.LENGTH_SHORT).show();
				return true;
			
			case R.id.actionmyblog:
				Toast.makeText(this, "개발자 블로그로 연결합니다.", Toast.LENGTH_SHORT).show();
				Intent actionBlogIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://naver.com"));
				startActivity(actionBlogIntent);
				return true;
			
			case R.id.actionmyemail:
				Toast.makeText(this, "개발자에게 메일을 보냅니다.", Toast.LENGTH_SHORT).show();
				Intent actionEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:teperi31@gmail.com"));
				startActivity(actionEmailIntent);
				return true;
			
			case R.id.actionmyphone:
				Intent actionTelIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-2061-3823"));
				int permissionCheck = ContextCompat.checkSelfPermission(RecyclerviewMoneyFlow.this, Manifest.permission.CALL_PHONE);
				
				if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "개발자에게 전화를 합니다.", Toast.LENGTH_SHORT).show();
					startActivity(actionTelIntent);
				} else {
					
					ActivityCompat.requestPermissions(RecyclerviewMoneyFlow.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
					
				}
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent actionTelIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-2061-3823"));
					Toast.makeText(this, "개발자에게 전화를 합니다.", Toast.LENGTH_SHORT).show();
					startActivity(actionTelIntent);
				} else {
					Toast.makeText(this, "이 기능은 통화 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show();
					
					Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					Uri uri = Uri.fromParts("package", getPackageName(), null);
					intent.setData(uri);
					startActivity(intent);
				}
				return;
			}
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
	
	

