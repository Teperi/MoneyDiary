package com.blogspot.teperi31.moneydiary;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
/*
* 목적 : UserList의 경우 FirebaseRecyclerView 로 설정하기 어려움
* 문제점 : query 를 실행할 때 Equalto 로는 같은 값만 가져오고 그 값이 아닌 값을 가져오지 못함
* 그에 따라 나와 다른 User 목록을 가져오기 어려움
* 해결 방법 : UserList 를 내 로컬 List 에 저장시킨 후 나와 같은 UID 제외 한 후 RecyclerView 실행
*
* */
public class AdapterMessengerUserList extends RecyclerView.Adapter<AdapterMessengerUserList.myViewHolder> {
	
	ArrayList<DataUser> UList;
	FirebaseUser User;
	DatabaseReference mDatabase;
	
	// 뷰 홀더와 layout 연결
	public static class myViewHolder extends RecyclerView.ViewHolder {
		CircleImageView ProfileImage;
		TextView ProfileName;
		
		
		myViewHolder(View view) {
			super(view);
			ProfileImage = view.findViewById(R.id.messenger_chatlist_row_profileCircleImage);
			ProfileName = view.findViewById(R.id.messenger_chatlist_row_profileNicknameText);
		}
	}
	
	AdapterMessengerUserList(ArrayList<DataUser> UserList) {
		this.UList = UserList;
	}
	
	// 틀 만들기
	@Override
	public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messenger_chatlist_row, viewGroup, false);
		return new myViewHolder(itemView);
	}
	
	// 틀 묶기
	@Override
	public void onBindViewHolder(@NonNull final myViewHolder holder, final int i) {
		// TODO : 사진 개인별 사진으로 바꿔주는 기능 추가해야 함
		holder.ProfileImage.setImageResource(R.drawable.cat3);
		// 내 닉네임 가져오기
		holder.ProfileName.setText(UList.get(i).NickName);
		// 뷰를 클릭했을 때 바로 대화창을 만들어서 이동하는 리스너 만들기
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				// 현재 유저와 데이터베이스 접속
				User = FirebaseAuth.getInstance().getCurrentUser();
				mDatabase = FirebaseDatabase.getInstance().getReference();
				
				// UserRooms : 유저의 채팅방 목록 저장
				// RoomType : 1:1채팅인지 그룹채팅인지 지정. 여기서는 현재 1:1 채팅만 지원
				// 단체채팅 기능은 그룹 만들기를 들어가면 체크박스로 선택할 수 있도록 다른 Activity로 만들기
				mDatabase.child("UserRooms/" + User.getUid())
						.orderByChild("RoomType").equalTo("Single")
						.addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
								// 몇개의 채팅방이 생겨있는지 확인하고 비교하기 위한 부분
								// 성능부분에선 문제가 될 수 있으나 현재 상용앱이 아니어서 빠르게 생각나는 방식으로 접근함
								int count = 0;
								// 만약 개인한테 만들어진 채팅방이 한개도 없다면
								if (dataSnapshot.getChildrenCount() <= 0) {
									// 새로운 채팅방 키를 만들고 채팅방 기본 데이터를 만듬
									String chatKey = createSingleChatRoom(i);
									// 채팅 화면으로 넘어감
									Intent intent = new Intent(v.getContext(), ChatActivity.class);
									intent.putExtra("ChatRoomKey", chatKey);
									v.getContext().startActivity(intent);
									// 버튼을 통해 채팅방에 접근하기 때문에 이 Activity 는 종료
									((Activity) v.getContext()).finish();
								} else {
									// 개인한테 만들어진 채팅방이 있다면 for문을 통해 확인
									loop1 : for (DataSnapshot item :
											dataSnapshot.getChildren()) {
										DataMessengerUserRoom RoomInfo = item.getValue(DataMessengerUserRoom.class);
										// 만들어진 채팅방에 상대방의 UID 가 있는 채팅방이 있다면?
										// 결국 : 과거의 채팅방을 만들어서 채팅한 데이터가 있다면
										if (RoomInfo.UserList.containsKey(UList.get(i).UID)) {
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
									// count 모든 채팅방을 확인해봤는데 상대방과의 채팅방이 없다면
									// 채팅방 새로 만들어 채팅 시작
									if (count == dataSnapshot.getChildrenCount()) {
										Log.d("test","여기 오면 안되는데..");
										String chatKey = createSingleChatRoom(i);
										Intent intent = new Intent(v.getContext(), ChatActivity.class);
										intent.putExtra("ChatRoomKey", chatKey);
										v.getContext().startActivity(intent);
										// 버튼을 통해 채팅방에 접근하기 때문에 이 Activity 는 종료
										((Activity) v.getContext()).finish();
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
	
	@Override
	public int getItemCount() {
		return UList.size();
	}
	
	public String getItem(int position) {
		return UList.get(position).UID;
	}
	
	// 채팅룸을 새로 만드는 메소드
	public String createSingleChatRoom(int position) {
		// 채팅방 키 제작
		String ChatRoomKey = mDatabase.child("Messenger/chatRoom").push().getKey();
		// UserList 를 각 유저마다 넣어주어야 하기 때문에 제작
		Map<String, Object> UserList = new HashMap<>();
		
		UserList.put(User.getUid(), true);
		UserList.put(UList.get(position).UID, true);
		
		// 채팅방 만들 때 사용할 내용 저장
		Map<String, Object> updateMyRoomList = new HashMap<>();
		Map<String, Object> updateYourRoomList = new HashMap<>();
		// 제목, 그룹or1:1, 유저 리스트 저장
		// 나에게는 상대방의 이름이, 상대방에게는 나의 이름이 나오도록 정리
		// 나머지 데이터는 모드 같으므로 같은 데이터 집어넣기
		updateMyRoomList.put("title", UList.get(position).NickName);
		updateYourRoomList.put("title", User.getDisplayName());
		updateMyRoomList.put("RoomType", "Single");
		updateYourRoomList.put("RoomType", "Single");
		updateMyRoomList.put("UserList", UserList);
		updateYourRoomList.put("UserList", UserList);
		updateMyRoomList.put("location", ChatRoomKey);
		updateYourRoomList.put("location", ChatRoomKey);
		updateMyRoomList.put("UserCount",2);
		updateYourRoomList.put("UserCount",2);
		
		// 각각 UID 마다 같은 정보를 저장해주기 위해서 저장
		Map<String, Object> childUpdates = new HashMap<>();
		childUpdates.put("/UserRooms/" + User.getUid() + "/" + ChatRoomKey, updateMyRoomList);
		childUpdates.put("/UserRooms/" + UList.get(position).UID + "/" + ChatRoomKey, updateYourRoomList);
		// 한번에 데이터베이스에 업데이트
		mDatabase.updateChildren(childUpdates);
		// 채팅방 키 리턴시켜서 intent 에 넘겨줌
		return ChatRoomKey;
	}
}
