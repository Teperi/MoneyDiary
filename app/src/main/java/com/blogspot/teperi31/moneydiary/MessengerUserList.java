package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MessengerUserList extends AppCompatActivity {
	
	//View 내 파일 연결
	Toolbar mToolbar;
	
	// 파이어베이스 연결
	private FirebaseUser mUser;
	private DatabaseReference mDatabase;
	
	private RecyclerView mRecyclerView;
	private FirebaseRecyclerAdapter<DataUser, ViewHolderChatUserList> mAdapter;
	private RecyclerView.LayoutManager mLayoutmanager;
	
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
		
		
		
		// 가져올 데이터 쿼리 정렬
		Query mUserDatabase = mDatabase.child("users").orderByChild("isCurrent").equalTo(false);
		
		// 데이터 가져오는 builder 설정
		FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataUser>()
				.setQuery(mUserDatabase, DataUser.class)
				.build();
		
		mAdapter = new FirebaseRecyclerAdapter<DataUser, ViewHolderChatUserList>(options) {
			
			@Override
			public ViewHolderChatUserList onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
				View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messenger_chatlist_row,viewGroup,false);
				return new ViewHolderChatUserList(itemView);
			}
			
			@Override
			protected void onBindViewHolder(@NonNull ViewHolderChatUserList holder, int position, @NonNull DataUser model) {
				holder.bindToChatList(model);
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), ChatActivity.class);
						startActivity(intent);
					}
				});
			}
		};
		mRecyclerView.setAdapter(mAdapter);
		
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// 어뎁터가 있으면 실시간 연결
		if (mAdapter != null) {
			mAdapter.startListening();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// 페이지가 멈출 때 실시간 연결기능 해제
		if (mAdapter != null) {
			mAdapter.stopListening();
		}
	}
}
