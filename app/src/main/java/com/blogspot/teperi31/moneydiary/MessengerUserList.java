package com.blogspot.teperi31.moneydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
* 이 앱에 가입한 전체 유저 리스트 보여주는 화면
* TODO : 추후 Floating 버튼을 통해 이 화면에 도달할수 있도록 설정/메신저 메인화면에는 채팅방 리스트 노출
* */

public class MessengerUserList extends AppCompatActivity {
	
	//View 내 파일 연결
	Toolbar mToolbar;
	
	// 파이어베이스 연결
	private FirebaseUser mUser;
	private DatabaseReference mDatabase;
	
	// 유저 리스트
	private ArrayList<DataUser> mUserList = new ArrayList<>();
	
	// 리사이클러뷰 관련
	private RecyclerView mRecyclerView;
	private AdapterMessengerUserList mAdapter;
	private RecyclerView.LayoutManager mLayoutmanager;
	
	//플로팅 버튼
	private FloatingActionButton mFAB;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messenger_chatlist);
		
		// 툴바 연결
		mToolbar = findViewById(R.id.messenger_chatlist_toolbarTop);
		setSupportActionBar(mToolbar);
		
		// 뒤로가기 버튼
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		
		
		
		// 유저 정보 가져오기
		mUser = FirebaseAuth.getInstance().getCurrentUser();
		// 데이터베이스 내 유저정보 가져오기
		mDatabase = FirebaseDatabase.getInstance().getReference();
		
		// 리사이클러뷰 연결
		
		mRecyclerView = findViewById(R.id.messenger_chatlist_recyclerview);
		mLayoutmanager = new LinearLayoutManager(this);
		
		mRecyclerView.setLayoutManager(mLayoutmanager);
		
		// USER 정보 데이터를 로컬 리스트에 담기
		mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot ds : dataSnapshot.getChildren()) {
					DataUser user = ds.getValue(DataUser.class);
					// 내 UID 뺴고 담도록 설정
					if (!user.UID.equals(mUser.getUid())) {
						mUserList.add(user);
					}
				}
				mAdapter = new AdapterMessengerUserList(mUserList);
				mRecyclerView.setAdapter(mAdapter);
				
				findViewById(R.id.messenger_chatlist_createchatGroup).setVisibility(View.VISIBLE);
				
				
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
		
		findViewById(R.id.messenger_chatlist_createchatGroup).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), MessengerGroupCreateList.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// 그룹 만들기 버튼 숨기기 & 네비게이션바 및 플로팅 버튼 보이기
		findViewById(R.id.messenger_chatlist_bottomBar).setVisibility(View.GONE);
		findViewById(R.id.messenger_chatlist_floatingbutton).setVisibility(View.GONE);
		findViewById(R.id.messenger_chatlist_createchatGroup).setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	// 뒤로가기 버튼 기능
	@Override
	public boolean onSupportNavigateUp() {
		MessengerUserList.super.onBackPressed();
		return true;
	}
}