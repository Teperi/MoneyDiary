package com.blogspot.teperi31.moneydiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;

public class RecyclerviewDiary extends AppCompatActivity {
	
	// 전화 걸기 권한 허용 요청
	final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
	
	//리사이클러뷰 설정용 인스턴스
	private RecyclerView mRecyclerView;
	private RecyclerView.LayoutManager mLayoutManager;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recyclerview_diary);
		
		// 액션 바 삽입
		// 추후 Dairy 전용 toolbar 생성
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop_list_diary);
		setSupportActionBar(myToolbar);
		
		mRecyclerView = findViewById(R.id.diary_recycler_view);
		
		// 리니어 레이아웃 매니저. 한줄씩 쌓임
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		
		// 정렬 기능:날짜 내림차순
		Collections.sort(ApplicationClass.dList, new Comparator<DataDiary>() {
			@Override
			public int compare(DataDiary o1, DataDiary o2) {
				return o2.DListDate.compareTo(o1.DListDate);
			}
		});
		
		// 어뎁터로 실제 데이터 연결
		AdapterDRecycler myAdapter = new AdapterDRecycler(ApplicationClass.dList);
		
		// 리사이클러뷰 어뎁터 연결 및 출력
		mRecyclerView.setAdapter(myAdapter);
		
		
	}
	
	//	toolbar 에 메뉴 띄워주는 함수
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topbar_actions_diary, menu);
		return true;
	}
	
	//	메뉴 버튼 클릭시 나올 상황 입력
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionAdd:
				Intent i = new Intent(RecyclerviewDiary.this, InputDiaryCreate.class);
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
				int permissionCheck = ContextCompat.checkSelfPermission(RecyclerviewDiary.this, Manifest.permission.CALL_PHONE);
				
				if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "개발자에게 전화를 합니다.", Toast.LENGTH_SHORT).show();
					startActivity(actionTelIntent);
				} else {
					
					ActivityCompat.requestPermissions(RecyclerviewDiary.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
					
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
