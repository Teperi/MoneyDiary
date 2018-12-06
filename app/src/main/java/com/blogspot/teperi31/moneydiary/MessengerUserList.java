package com.blogspot.teperi31.moneydiary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessengerUserList extends AppCompatActivity {
	
	//View 내 파일 연결
	Toolbar mToolbar;
	
	// 파이어베이스 연결
	private FirebaseUser mUser;
	private DatabaseReference mDatabase;
	
	// 유저 리스트
	private ArrayList<DataUser> mUserList = new ArrayList<>();
	
	private RecyclerView mRecyclerView;
	private AdapterMessengerUserList mAdapter;
	private RecyclerView.LayoutManager mLayoutmanager;
	
	//플로팅 버튼
	private FloatingActionButton fab;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messenger_chatlist);
		
		// 툴바 연결
		mToolbar = findViewById(R.id.messenger_chatlist_toolbarTop);
		setSupportActionBar(mToolbar);
		
		// 유저 정보 가져오기
		mUser = FirebaseAuth.getInstance().getCurrentUser();
		// 데이터베이스 내 유저정보 가져오기
		mDatabase = FirebaseDatabase.getInstance().getReference();
		
		// 리사이클러뷰 연결
		
		mRecyclerView = findViewById(R.id.messenger_chatlist_recyclerview);
		mLayoutmanager = new LinearLayoutManager(this);
		
		mRecyclerView.setLayoutManager(mLayoutmanager);
		
		// 데이터 리스트에 담기
		mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot ds : dataSnapshot.getChildren()) {
					DataUser user = ds.getValue(DataUser.class);
					if (!user.UID.equals(mUser.getUid())) {
						mUserList.add(user);
					}
				}
				mAdapter = new AdapterMessengerUserList(mUserList);
				mRecyclerView.setAdapter(mAdapter);
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
}