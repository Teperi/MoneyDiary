package com.blogspot.teperi31.moneydiary;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
	
	String saveDate = null;
	// 날짜를 저장하는 String
	int saveDateYear = 0;
	int saveDateMonth = 0;
	int saveDateDay = 0;
	
	// 전화 걸기 권한 허용 요청
	final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
	
	// Date 타입을 String 으로 바꿔주는 기능을 가진 함수
	// yyyy-MM-dd 형식으로 변경하는 틀을 저장함
	SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// action bar 생성
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop_basic);
		setSupportActionBar(myToolbar);
		
		
		final CalendarView calendarView = findViewById(R.id.calender1);
		calendarView.setDate(System.currentTimeMillis());
		
		
		
		saveDate = transFormat.format(calendarView.getDate());
		
		
		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				saveDateYear = year;
				saveDateMonth = month + 1;
				saveDateDay = dayOfMonth;
				saveDate = (year + "-" + (month + 1) + "-" + dayOfMonth);
				Toast.makeText(MainActivity.this, year + "-" + (month + 1) + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
				
				
			}
		});
		
		// 버튼 누를 시 화면 전환
		// 다이어리 이동
		findViewById(R.id.testbutton1).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				Intent i = new Intent(MainActivity.this, RecyclerviewDiary.class);
				startActivity(i);
				
				
			}
		});
		
		// 가계부 이동
		findViewById(R.id.testbutton2).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(MainActivity.this, RecyclerviewMoneyFlow.class);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.signup_intent).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(MainActivity.this, SignupActivity.class);
				startActivity(intent);
				
			}
		});
		
		
	}
	
	//	toolbar 에 메뉴 띄워주는 함수
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topbar_actions_main, menu);
		return true;
	}
	
	
	//	메뉴 버튼 클릭시 나올 상황 입력
	//  일단 toast만 연결함 / 추후 Intent 기능 넣어야 함
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
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
				int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE);
				
				if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "개발자에게 전화를 합니다.", Toast.LENGTH_SHORT).show();
					startActivity(actionTelIntent);
				} else {
					
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
				}
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	// 권한 설정 코드가 들어올 경우 권한 묻기
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

