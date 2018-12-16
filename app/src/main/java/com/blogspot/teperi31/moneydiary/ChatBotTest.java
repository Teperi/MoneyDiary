package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class ChatBotTest extends AppCompatActivity implements AIListener {
	
	public static final String MESSAGES_CHILD = "messages";
	
	// 로딩바, topbar, 버튼 등등
	private ProgressBar mProgressBar;
	private Toolbar mTopBar;
	
	// 전체 서버 데이터 연결
	private DatabaseReference mDatabase;
	
	// 유저 연결
	private FirebaseUser mFirebaseUser;
	
	// 채팅 데이터
	private DatabaseReference mDatabaseChatRoom;
	private DatabaseReference mDatabaseMyUserRoom;
	private DatabaseReference mDatabaseOtherUserRoom;
	private DatabaseReference mDatabaseOtherUserList;
	
	private String mUserNickname;
	private String mUserPhoto;
	
	// 리싸이클러뷰 관련 연결
	private RecyclerView mRecycler;
	private LinearLayoutManager mLayoutManager;
	private FirebaseRecyclerAdapter<DataChatContent, RecyclerView.ViewHolder> mFirebaseChatContentAdapter;
	
	private String ChatRoomKey;
	
	// 내용을 보내기 위한 text 및 버튼 연결
	private MaterialButton mSendButton;
	private EditText mSendText;
	private ImageView mSendImage;
	
	//챗봇 데이터 저장
	ArrayList<String> OtherUIDList = new ArrayList<>();
	
	// 가위바위봇 가져오는 변수
	private AIService aiService;
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messenger_chatcontent);
		
		
		
		mDatabase = FirebaseDatabase.getInstance().getReference();
		
		DataUser datauser;
		
		datauser = new DataUser("chatbot", "chatbot","chatbot@test.com", null);
		Map<String, Object> inputUserData = datauser.toMap();
		Map<String, Object> childUpdates = new HashMap<>();
		// Map 에 한번에 저장 후
		childUpdates.put("/users/" + "chatbot", inputUserData);
		// 데이터베이스에 집어넣기
		mDatabase.updateChildren(childUpdates);
		
		
		
		mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		// 유저가 없으면 로그인 창으로 바로 이동
		if (mFirebaseUser == null) {
			Toast.makeText(this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, SignInAccountInfo.class));
			finish();
			return;
		}
		// 유저가 있는 경우 닉네임과 사진 가져오기
		// 사진이 없는 경우 기본 사진 보이기
		else {
			mUserNickname = mFirebaseUser.getDisplayName();
			if (mFirebaseUser.getPhotoUrl() != null) {
				mUserPhoto = mFirebaseUser.getPhotoUrl().toString();
			}
		}
		// 화면 내 버튼들과 변수 연결
		mProgressBar = findViewById(R.id.messenger_chatcontent_ProgressBar);
		
		// 액션 바 보이기
		mTopBar = findViewById(R.id.messenger_chatcontent_toolbarTop);
		setSupportActionBar(mTopBar);
		
		// 리사이클러뷰 연결 & 고정
		mRecycler = findViewById(R.id.messenger_chatcontent_recyclerview);
		mRecycler.setHasFixedSize(true);
		
		
		
		// 세로로 쌓기 기능
		mLayoutManager = new LinearLayoutManager(this);
		// Query 에 있는 정렬 기능을 역순으로 해 주는 부분
//		mLayoutManager.setReverseLayout(true);
//		mLayoutManager.setStackFromEnd(true);
		// 리사이클러뷰 layout 연결
		mRecycler.setLayoutManager(mLayoutManager);
		
		// 데이터베이스 연결
		// 메시지가 쌓이는 곳의 채팅방 데이터
		mDatabaseChatRoom = FirebaseDatabase.getInstance().getReference().child("Messenger").child("chatRoom");
		// 내 UserRoom 데이터
		mDatabaseMyUserRoom = FirebaseDatabase.getInstance().getReference().child("UserRooms").child(mFirebaseUser.getUid());
		// 상대방 UserRoom 연결용
		mDatabaseOtherUserRoom = FirebaseDatabase.getInstance().getReference().child("UserRooms");
		
		
		// 쿼리로 가져오기
		// 가져올 데이터 쿼리
		ChatRoomKey = createSingleChatRoom();
		
		OtherUIDList.add("chatbot");
		
		// 내 채팅방에 안읽은 메세지 수 삭제
		mDatabaseMyUserRoom.child(ChatRoomKey).child("UnReadMessageCount").removeValue();
		
		Query ChatContentQuery = mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD).orderByChild("dateTime");
		
		// 데이터 가져오는 builder 설정
		final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataChatContent>()
				.setQuery(ChatContentQuery, DataChatContent.class)
				.build();
		
		// AI 설정 (가위바위봇 설정)
		final AIConfiguration config = new AIConfiguration("368bd415dab144a4a5bad975c7eed151",
				AIConfiguration.SupportedLanguages.Korean,
				AIConfiguration.RecognitionEngine.System);
		// 설정된 봇 가져오기
		aiService = AIService.getService(this, config);
		aiService.setListener(this);
		
		final AIDataService aiDataService = new AIDataService(config);
		
		final AIRequest aiRequest = new AIRequest();
		
		
		// 어뎁터 설정
		mFirebaseChatContentAdapter = new FirebaseRecyclerAdapter<DataChatContent, RecyclerView.ViewHolder>(options) {
			private static final int MY_MESSAGE = 1;
			private static final int OTHER_MESSAGE = 2;
			
			// View 타입 결정
			// 내가 보낸 메시지의 경우 MY_MESSAGE 사용
			// 남이 보낸 메시지는 OTHER_MESSAGE 사용
			@Override
			public int getItemViewType(int position) {
				DataChatContent message = getItem(position);
				if (message.getId().equals(mFirebaseUser.getUid())) {
					return MY_MESSAGE;
				} else {
					return OTHER_MESSAGE;
				}
			}
			
			// 뷰홀더 만들기
			// 내가 만든 메시지의 경우 ViewHolderChatSent 모양 따라가기
			// 아닐 경우 ViewHolderChatRecived 모양 따라가기
			@NonNull
			@Override
			public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
				if (i == MY_MESSAGE) {
					LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
					return new ViewHolderChatSent(inflater.inflate(R.layout.messenger_chatcontent_sent_row, viewGroup, false));
				} else {
					LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
					return new ViewHolderChatRecived(inflater.inflate(R.layout.messenger_chatcontent_recived_row, viewGroup, false));
				}
			}
			
			
			// Bind
			// 뷰홀더와 같이 모양 따라가기
			@Override
			protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, DataChatContent model) {
				mProgressBar.setVisibility(View.INVISIBLE);
				if (getItemViewType(position) == MY_MESSAGE) {
					// ChatRoomKey를 넘겨줘서 ChatRoomKey 안의 UnReadCount 에 접근할 수 있도록 함
					((ViewHolderChatSent) holder).bindToChat(model, ChatRoomKey);
				} else {
					((ViewHolderChatRecived) holder).bindToChat(model, ChatRoomKey);
				}
			}
			
			
		};
		// 아직 얘가 뭐하는건지는 정확히 파악을 못했는데.....
		// TODO : 파악하기 / 파악해서 안읽은 포지션으로 보내는 방법 찾기
		mFirebaseChatContentAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				int friendlyMessageCount = mFirebaseChatContentAdapter.getItemCount();
				int lastVisiblePosition =
						mLayoutManager.findLastCompletelyVisibleItemPosition();
				// If the recycler view is initially being loaded or the
				// user is at the bottom of the list, scroll to the bottom
				// of the list to show the newly added message.
				if (lastVisiblePosition == -1 ||
						(positionStart >= (friendlyMessageCount - 1) &&
								lastVisiblePosition == (positionStart - 1))) {
					mRecycler.scrollToPosition(positionStart);
				}
			}
		});
		// 어뎁터 연결
		mRecycler.setAdapter(mFirebaseChatContentAdapter);
		
		
		// 텍스트가 1개라도 있으면 버튼 활성화
		// 텍스트가 없는 경우 버튼 비활성화
		mSendText = findViewById(R.id.messenger_chatcontent_addEditText);
		mSendText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (charSequence.toString().trim().length() > 0) {
					mSendButton.setEnabled(true);
				} else {
					mSendButton.setEnabled(false);
				}
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		
		// 전송 버튼 누를 경우
		mSendButton = findViewById(R.id.messenger_chatcontent_sendButton);
		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				// 데이터가 2번 이상 들어가지 않도록 버튼 비활성화
				mSendButton.setEnabled(false);
				// 데이터 넣어주는 부분
				mDatabaseMyUserRoom.child(ChatRoomKey).addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						// 채팅방 내 메시지부분의 키 가져오기
						String key = mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
								.push().getKey();
						// 마지막 채팅 내용 및 시간 기록
						final String LastText = mSendText.getText().toString();
						final Long LastTime = System.currentTimeMillis();
						// Chat message 저장
						final DataChatContent dataChatContent = new
								DataChatContent(mFirebaseUser.getUid(),
								LastText,
								mUserNickname,
								mUserPhoto,
								null /* no image */,
								LastTime,
								dataSnapshot.getValue(DataMessengerUserRoom.class).UserCount,
								key);
						// 데이터 넣어주고
						mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
								.child(key).setValue(dataChatContent);
						// 마지막 채팅 내용 채팅방에 띄우기 위해 저장 - 내 채팅방
						mDatabaseMyUserRoom.child(ChatRoomKey).child("lastMessage").setValue(LastText);
						mDatabaseMyUserRoom.child(ChatRoomKey).child("lastTime").setValue(LastTime);
						
						// 마지막 채팅 내용 채팅방에 띄우기 위해 저장 - 상대방 채팅방
						// 채팅방의 다른 유저 수 만큼 돌리기
						for (String UID : OtherUIDList) {
							// 상대방의 채팅방 정보에 접근
							mDatabaseOtherUserRoom.child(UID).child(ChatRoomKey).addListenerForSingleValueEvent(new ValueEventListener() {
								@Override
								public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
									// 만약 안읽은 메시지 수가 없으면
									if (!dataSnapshot.child("UnReadMessageCount").exists()) {
										// 안읽은 수 1개
										dataSnapshot.child("UnReadMessageCount").getRef().setValue(1);
									}
									// 만약 안읽은 메시지 수가 있다면
									else {
										// 안읽은 수 +1
										dataSnapshot.child("UnReadMessageCount").getRef().setValue((Long) dataSnapshot.child("UnReadMessageCount").getValue() + 1);
									}
									// 최근 온 채팅 글
									dataSnapshot.child("lastMessage").getRef().setValue(LastText);
									// 최근 온 채팅 시간
									dataSnapshot.child("lastTime").getRef().setValue(LastTime);
								}
								
								@Override
								public void onCancelled(@NonNull DatabaseError databaseError) {
								
								}
							});
						}
						
						aiService.startListening();
						
						String message = mSendText.getText().toString();
						if(!message.equals("")){
							aiRequest.setQuery(message);
							new AsyncTask<AIRequest,Void,AIResponse>(){
								
								@Override
								protected AIResponse doInBackground(AIRequest... aiRequests) {
									final AIRequest request = aiRequests[0];
									try {
										Log.d("DialogFlowAsyncTask","Request" + aiRequest.toString());
										final AIResponse response = aiDataService.request(aiRequest);
										return response;
									} catch (AIServiceException e) {
									}
									return null;
								}
								@Override
								protected void onPostExecute(AIResponse response) {
									if (response != null) {
										
										Result result = response.getResult();
										String reply = result.getFulfillment().getSpeech();
										DataChatContent botChatMessage = new DataChatContent("chatbot",
												reply,
												"chatbot",
												null,
												null,
												System.currentTimeMillis(),
												2L,
												"bot");
										String key = mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
												.push().getKey();
										mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
												.child(key).setValue(botChatMessage);
									}
								}
							}.execute(aiRequest);
						}
						
						
						// 텍스트 다 지우고
						mSendText.setText("");
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
					
					}
				});
			}
		});
		
		
	}
	
	@Override
	public void onPause() {
		// 데이터 실시간 변경사항 체크 해제
		mFirebaseChatContentAdapter.stopListening();
		super.onPause();
	}
	
	@Override
	protected void onStart() {
		// 데이터 실시간 변경사항 체크
		mFirebaseChatContentAdapter.startListening();
		super.onStart();
	}
	
	
	// 채팅룸을 새로 만드는 메소드
	public String createSingleChatRoom() {
		// 채팅방 키 제작
		String ChatRoomKey = mDatabase.child("Messenger/chatRoom").push().getKey();
		// UserList 를 각 유저마다 넣어주어야 하기 때문에 제작
		Map<String, Object> UserList = new HashMap<>();
		
		UserList.put(mFirebaseUser.getUid(), true);
		UserList.put("chatbot", true);
		
		// 채팅방 만들 때 사용할 내용 저장
		Map<String, Object> updateMyRoomList = new HashMap<>();
		Map<String, Object> updateYourRoomList = new HashMap<>();
		// 제목, 그룹or1:1, 유저 리스트 저장
		// 나에게는 상대방의 이름이, 상대방에게는 나의 이름이 나오도록 정리
		// 나머지 데이터는 모드 같으므로 같은 데이터 집어넣기
		updateMyRoomList.put("title", "chatbot");
		updateYourRoomList.put("title", mFirebaseUser.getDisplayName());
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
		childUpdates.put("/UserRooms/" + mFirebaseUser.getUid() + "/" + ChatRoomKey, updateMyRoomList);
		childUpdates.put("/UserRooms/" + "chatbot" + "/" + ChatRoomKey, updateYourRoomList);
		// 한번에 데이터베이스에 업데이트
		mDatabase.updateChildren(childUpdates);
		// 채팅방 키 리턴시켜서 intent 에 넘겨줌
		return ChatRoomKey;
	}
	
	@Override
	public void onResult(ai.api.model.AIResponse response) {
	
	}
	
	@Override
	public void onError(ai.api.model.AIError error) {
	
	}
	
	@Override
	public void onAudioLevel(float level) {
	
	}
	
	@Override
	public void onListeningStarted() {
	
	}
	
	@Override
	public void onListeningCanceled() {
	
	}
	
	@Override
	public void onListeningFinished() {
	
	}
}
