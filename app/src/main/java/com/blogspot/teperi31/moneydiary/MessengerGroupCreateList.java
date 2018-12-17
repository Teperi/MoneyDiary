package com.blogspot.teperi31.moneydiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessengerGroupCreateList extends AppCompatActivity {
	//View 내 파일 연결
	Toolbar mToolbar;
	MaterialButton createComplete;
	
	// 파이어베이스 연결
	private FirebaseUser mUser;
	private DatabaseReference mDatabase;
	
	// 유저 리스트
	private ArrayList<DataUser> mUserList = new ArrayList<>();
	
	// 리사이클러뷰 관련
	private RecyclerView mRecyclerView;
	private AdapterMessengerGroupCreateList mAdapter;
	private RecyclerView.LayoutManager mLayoutmanager;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messenger_chatlist_groupcreate);
		
		// 툴바 연결
		mToolbar = findViewById(R.id.messenger_chatlist_groupcreate_toobarTop);
		setSupportActionBar(mToolbar);
		
		// 뒤로가기 버튼
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		// 유저 정보 가져오기
		mUser = FirebaseAuth.getInstance().getCurrentUser();
		// 데이터베이스 내 유저정보 가져오기
		mDatabase = FirebaseDatabase.getInstance().getReference();
		
		// 리사이클러뷰 연결
		
		mRecyclerView = findViewById(R.id.messenger_chatlist_groupcreate_recyclerview);
		mLayoutmanager = new LinearLayoutManager(this);
		
		mRecyclerView.setLayoutManager(mLayoutmanager);
		
		// USER 정보 데이터를 로컬 리스트에 담기
		mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot ds : dataSnapshot.getChildren()) {
					DataUser user = ds.getValue(DataUser.class);
					// 내 UID 및 챗봇 UID 빼고 리스트 담기
					if (user.UID.equals(mUser.getUid()) ||
							user.UID.equals("RSPbot") ||
							user.UID.equals("Inputbot")) {
						
					} else {
						mUserList.add(user);
					}
				}
				mAdapter = new AdapterMessengerGroupCreateList(MessengerGroupCreateList.this, mUserList);
				mRecyclerView.setAdapter(mAdapter);
				
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
		
		createComplete = findViewById(R.id.messenger_chatlist_groupcreate_complete);
		createComplete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				mDatabase.child("UserRooms").child(mUser.getUid()).orderByChild("RoomType").equalTo("Group").addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						String ChatRoomKey = null;
						// 그룹 채팅방이 한개도 없다면..
						if (dataSnapshot.getChildrenCount() <= 0) {
							// 그룹 채팅방 만들기
							createGroupChatRoom(mAdapter.CheckedUserList);
							
						} else {
							int count = 0;
							loop1:
							for (DataSnapshot item :
									dataSnapshot.getChildren()) {
								DataMessengerUserRoom RoomInfo = item.getValue(DataMessengerUserRoom.class);
								Set<String> CheckUserListSet = new HashSet<>();
								for (int i = 0; i < mAdapter.CheckedUserList.size(); i++) {
									CheckUserListSet.add(mAdapter.CheckedUserList.get(i).UID);
								}
								CheckUserListSet.add(mUser.getUid());
								// 만들어진 채팅방에 상대방의 UID 가 있는 채팅방이 있다면?
								// 결국 : 과거의 채팅방을 만들어서 채팅한 데이터가 있다면
								// 여기서 두가지 비교: 인원수 같아? 그리고 인원 리스트가 같아?
								if (RoomInfo.UserCount == mAdapter.CheckedUserList.size() || RoomInfo.UserList.keySet().equals(CheckUserListSet)) {
									// 그 채팅방의 Key 값을 넘겨서 채팅 시작
									Intent intent = new Intent(v.getContext(), ChatActivity.class);
									intent.putExtra("ChatRoomKey", RoomInfo.location);
									v.getContext().startActivity(intent);
									// 버튼을 통해 채팅방에 접근하기 때문에 이 Activity 는 종료
									((Activity) v.getContext()).finish();
									// count 가 늘어나지 않도록 for문 빠져나오기
									break loop1;
								}
								count++;
							}
							// 다 비교하고 왔는데도 없으면?
							if (count == dataSnapshot.getChildrenCount()) {
								// 그룹 채팅방 만들기
								createGroupChatRoom(mAdapter.CheckedUserList);
								
							}
						}
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
					
					}
				});
				
			}
		});
		
	}
	
	// 뒤로가기 버튼 기능
	@Override
	public boolean onSupportNavigateUp() {
		MessengerGroupCreateList.super.onBackPressed();
		return true;
	}
	
	// 채팅룸을 새로 만드는 메소드
	public void createGroupChatRoom(final ArrayList<DataUser> CheckedUserList) {
		// 채팅방 키 제작
		final String ChatRoomKey = mDatabase.child("Messenger/chatRoom").push().getKey();
		// 내 정보 받아와서 UserList에 같이 넣기
		mDatabase.child("users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				//내 정보 저장
				DataUser myUserInfo = dataSnapshot.getValue(DataUser.class);
				// 그룹채팅방이 될 모든 사람과 묶기
				CheckedUserList.add(myUserInfo);
				// UserList 만들기
				Map<String, Object> UserList = new HashMap<>();
				for (int i = 0; i < CheckedUserList.size(); i++) {
					UserList.put(CheckedUserList.get(i).UID, true);
				}
				// 각각에 맞는 정보를 넣어서 Database에 넣어야 함
				// 그룹채팅방은 사람 수가 그때 그때 달라지기 때문에 for문으로 해결함
				for (int i = 0; i < CheckedUserList.size(); i++) {
					Map<String, Object> updateRoomList = new HashMap<>();
					// 제목의 경우 현재 내 이름이 안들어가게 하기 위해 나를 제외한 인원이 있는 Arraylist 만들기
					ArrayList<DataUser> titleUser = new ArrayList<>();
					titleUser.addAll(CheckedUserList);
					titleUser.remove(CheckedUserList.get(i));
					//타이틀 텍스트 만들기
					String titleText = "";
					// for 문으로 텍스트를 계속 뒤에 이어붙이는 형식으로 실행
					for (int j = 0; j < titleUser.size(); j++) {
						if (titleText.length() <= 0) {
							titleText = (String) (titleUser.get(j).NickName);
						} else {
							titleText = (String) (titleText + ", " + titleUser.get(j).NickName);
						}
						
					}
					// 타이틀 텍스트 집어넣기
					updateRoomList.put("title", titleText);
					// 그룹 채팅방은 무조건 RoomType 이 Group
					updateRoomList.put("RoomType", "Group");
					// UserList는 위에서 만든 대로 실행
					updateRoomList.put("UserList", UserList);
					// 키는 위에서 만든것 사용
					updateRoomList.put("location", ChatRoomKey);
					// 사이즈는 인원수로 실행
					updateRoomList.put("UserCount", CheckedUserList.size());
					
					mDatabase.child("UserRooms").child(CheckedUserList.get(i).UID).child(ChatRoomKey).updateChildren(updateRoomList);
					
					// 채팅 화면으로 넘어감
					Intent intent = new Intent(MessengerGroupCreateList.this, ChatActivity.class);
					intent.putExtra("ChatRoomKey", ChatRoomKey);
					startActivity(intent);
					// 버튼을 통해 채팅방에 접근하기 때문에 이 Activity 는 종료
					finish();
					
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				throw new IllegalArgumentException("그룹채팅방 만들기 실패");
			}
		});
	}
}
