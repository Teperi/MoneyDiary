package com.blogspot.teperi31.moneydiary;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;
import java.text.SimpleDateFormat;



public class MainActivity extends AppCompatActivity {
	
	String saveDate = null;
	// 날짜를 저장하는 String
	int saveDateYear = 0;
	int saveDateMonth = 0;
	int saveDateDay = 0;
	
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("TestAppActivity","onStop");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("TestAppActivity","onDestroy");
	}
	
	// Date 타입을 String 으로 바꿔주는 기능을 가진 함수
	// yyyy-MM-dd 형식으로 변경하는 틀을 저장함
	SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop_main);
		setSupportActionBar(myToolbar);
		
		
		
		
		
		
		final CalendarView calendarView = findViewById(R.id.calender1);
		calendarView.setDate(System.currentTimeMillis());
		
		saveDate = transFormat.format(calendarView.getDate());
		
		
		
		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				saveDateYear = year;
				saveDateMonth = month+1;
				saveDateDay = dayOfMonth;
				saveDate = (year+"-"+(month+1)+"-"+dayOfMonth);
				Toast.makeText(MainActivity.this, year+"-"+(month+1)+"-"+dayOfMonth , Toast.LENGTH_SHORT).show();
				
				
			}
		});
		
		// 버튼 누를 시 화면 전환
		findViewById(R.id.testbutton).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				//날짜
				Intent intent = new Intent(MainActivity.this,test.class);
				intent.putExtra("saveDate",saveDate);
				startActivity(intent);
			}
		});
		
		
		findViewById(R.id.testbutton2).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				//날짜
				Intent intent = new Intent(MainActivity.this,Recyclerviewtest.class);
				startActivity(intent);
			}
		});
		

		
		
	}
	
	//	toolbar 에 메뉴 띄워주는 함수
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topbar_actions, menu);
		return true;
	}
	
	
	//	메뉴 버튼 클릭시 나올 상황 입력
	//  일단 toast만 연결함 / 추후 Intent 기능 넣어야 함
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionSearch:
				Toast.makeText(this, "검색", Toast.LENGTH_SHORT).show();
				return true;
				
			case R.id.actionAdd:
				Intent i = new Intent(MainActivity.this, SpendCreateInput.class);
				startActivity(i);
				Toast.makeText(this, "추가", Toast.LENGTH_SHORT).show();
				return true;
				
			case R.id.actionnotice:
				Toast.makeText(this, "공지사항", Toast.LENGTH_SHORT).show();
				return true;
			
			case R.id.actionmyblog:
				Toast.makeText(this, "개발자 블로그로 연결합니다.", Toast.LENGTH_SHORT).show();
				return true;
			
			case R.id.actionmyemail:
				Toast.makeText(this, "개발자에게 메일을 보냅니다.", Toast.LENGTH_SHORT).show();
				return true;
				
			case R.id.actionappintroduce:
				Toast.makeText(this, "앱 소개 페이지", Toast.LENGTH_SHORT).show();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
