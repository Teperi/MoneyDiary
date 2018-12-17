package com.blogspot.teperi31.moneydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ai.api.AIListener;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;

public class ChatActivity extends AppCompatActivity implements AIListener {
	// Child 정보
	public static final String MESSAGES_CHILD = "messages";
	private static final int REQUEST_INVITE = 1;
	private static final int REQUEST_IMAGE = 2;
	private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
	public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
	public static final String ANONYMOUS = "anonymous";
	private static final String MESSAGE_SENT_EVENT = "message_sent";
	
	
	// 유저 정보
	private FirebaseUser mFirebaseUser;
	private String mUserNickname;
	private String mUserPhoto;
	
	// 뷰 필요 연결
	// 로딩바, topbar, 버튼 등등
	private ProgressBar mProgressBar;
	private Toolbar mTopBar;
	
	// 내용을 보내기 위한 text 및 버튼 연결
	private MaterialButton mSendButton;
	private EditText mSendText;
	private ImageView mSendImage;
	
	
	// 리싸이클러뷰 관련 연결
	private RecyclerView mRecycler;
	private LinearLayoutManager mLayoutManager;
	private FirebaseRecyclerAdapter<DataChatContent, RecyclerView.ViewHolder> mFirebaseChatContentAdapter;
	
	// 챗봇 입력봇 데이터 넣어주기
	private DatabaseReference mDatabase;
	
	// 채팅 데이터
	private DatabaseReference mDatabaseChatRoom;
	private DatabaseReference mDatabaseMyUserRoom;
	private DatabaseReference mDatabaseOtherUserRoom;
	private DatabaseReference mDatabaseOtherUserList;
	private FirebaseMessaging fm;
	
	// 가위바위봇 가져오는 변수
	private AIService aiService;
	AIDataService aiDataService;
	AIRequest aiRequest;
	
	//채팅방에 있는 다른 유저 UID 저장
	private Map<String, Boolean> OtherUIDInfo = new HashMap<>();
	ArrayList<String> OtherUIDList = new ArrayList<>();
	
	private String ChatRoomKey;
	private String BotType;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messenger_chatcontent);
		
		mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		mDatabase = FirebaseDatabase.getInstance().getReference();
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
		
		// 푸시 메시지 연결
		fm = FirebaseMessaging.getInstance();
		
		
		// 화면 내 버튼들과 변수 연결
		mProgressBar = findViewById(R.id.messenger_chatcontent_ProgressBar);
		
		// 액션 바 보이기
		mTopBar = findViewById(R.id.messenger_chatcontent_toolbarTop);
		setSupportActionBar(mTopBar);
		
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
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
		
		// 봇인지 아닌지 구분하기
		ChatRoomKey = getIntent().getStringExtra("ChatRoomKey");
		if (ChatRoomKey == null) {
			Log.w("test", "채팅방을 만들 때 키가 안넘어 왔다는데?");
		}
		
		mDatabaseMyUserRoom.child(ChatRoomKey).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				BotType = "No";
				for (DataSnapshot item : dataSnapshot.child("UserList").getChildren()) {
					if (item.getKey().equals("RSPbot")) {
						Log.i("test", mDatabaseMyUserRoom.child(ChatRoomKey).child("UserList").child("RSPbot").getKey());
						BotType = "RSPbot";
					} else if (item.getKey().equals("Inputbot")) {
						BotType = "Inputbot";
					}
				}
				if (BotType.equals("RSPbot")) {
					Log.i("test", "가위바위봇 설정하기");
					// AI 설정 (가위바위봇 설정)
					final AIConfiguration config = new AIConfiguration("368bd415dab144a4a5bad975c7eed151",
							AIConfiguration.SupportedLanguages.Korean,
							AIConfiguration.RecognitionEngine.System);
					// 설정된 봇 가져오기
					aiService = AIService.getService(ChatActivity.this, config);
					aiService.setListener(ChatActivity.this);
					
					aiDataService = new AIDataService(config);
					
					aiRequest = new AIRequest();
				} else if (BotType.equals("Inputbot")) {
					Log.i("test", "입력봇 설정하기");
					// AI 설정 (가위바위봇 설정)
					final AIConfiguration config = new AIConfiguration("fe1ea227eaa945a4940fd3e3c08f71bf",
							AIConfiguration.SupportedLanguages.Korean,
							AIConfiguration.RecognitionEngine.System);
					// 설정된 봇 가져오기
					aiService = AIService.getService(ChatActivity.this, config);
					aiService.setListener(ChatActivity.this);
					
					aiDataService = new AIDataService(config);
					
					aiRequest = new AIRequest();
				}
				
				mTopBar.setTitle(dataSnapshot.child("title").getValue().toString());
				
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
		
		
		// 상대방 UserList 받아오기
		mDatabaseOtherUserList = FirebaseDatabase.getInstance().getReference().child("UserRooms").child(mFirebaseUser.getUid()).child(ChatRoomKey).child("UserList");
		mDatabaseOtherUserList.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				// 채팅방에 포함된 전체 리스트 받아와서
				OtherUIDInfo = (Map<String, Boolean>) dataSnapshot.getValue();
				// 내 UID 제외
				OtherUIDInfo.remove(mFirebaseUser.getUid());
				OtherUIDList.addAll(OtherUIDInfo.keySet());
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
		
		// 내 채팅방에 안읽은 메세지 수 삭제
		mDatabaseMyUserRoom.child(ChatRoomKey).child("UnReadMessageCount").removeValue();
		
		
		Query ChatContentQuery = mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD).orderByChild("dateTime");
		
		// 데이터 가져오는 builder 설정
		final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataChatContent>()
				.setQuery(ChatContentQuery, DataChatContent.class)
				.build();
		
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
				if (getItemViewType(position) == MY_MESSAGE) {
					// ChatRoomKey를 넘겨줘서 ChatRoomKey 안의 UnReadCount 에 접근할 수 있도록 함
					((ViewHolderChatSent) holder).bindToChat(model, ChatRoomKey);
				} else {
					((ViewHolderChatRecived) holder).bindToChat(model, ChatRoomKey);
				}
			}
			
			
		};
		// 가장 아래로 내려주는 부분
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

//		// 이미지 전송
//		mSendImage = findViewById(R.id.messenger_chatcontent_addImageView);
//		mSendImage.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//				intent.addCategory(Intent.CATEGORY_OPENABLE);
//				intent.setType("image/*");
//				startActivityForResult(intent, REQUEST_IMAGE);
//			}
//		});
		
		// 전송 버튼 누를 경우
		mSendButton = findViewById(R.id.messenger_chatcontent_sendButton);
		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mProgressBar.setVisibility(View.VISIBLE);
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
						// 만약 챗봇이라면 읽는 수 없애기
						final DataChatContent dataChatContent;
						if (BotType.equals("No")) {
							dataChatContent = new
									DataChatContent(mFirebaseUser.getUid(),
									LastText,
									mUserNickname,
									mUserPhoto,
									null /* no image */,
									LastTime,
									dataSnapshot.getValue(DataMessengerUserRoom.class).UserCount,
									key);
						} else {
							dataChatContent = new
									DataChatContent(mFirebaseUser.getUid(),
									LastText,
									mUserNickname,
									mUserPhoto,
									null /* no image */,
									LastTime,
									1L,
									key);
						}
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
									if (BotType.equals("No")) {
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
									} else {
									
									}
									mProgressBar.setVisibility(View.GONE);
								}
								
								@Override
								public void onCancelled(@NonNull DatabaseError databaseError) {
								
								}
							});
						}
						
						if (BotType.equals("RSPbot")) {
							aiService.startListening();
							
							String message = mSendText.getText().toString();
							if (!message.equals("")) {
								aiRequest.setQuery(message);
								new AsyncTask<AIRequest, Void, AIResponse>() {
									
									@Override
									protected void onPreExecute() {
										super.onPreExecute();
										mSendText.setHint("가위바위봇이 당신의 데이터를 읽고 있어요...");
										mSendButton.setEnabled(false);
									}
									
									@Override
									protected AIResponse doInBackground(AIRequest... aiRequests) {
										final AIRequest request = aiRequests[0];
										try {
											Log.d("DialogFlowAsyncTask", "Request" + aiRequest.toString());
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
											Log.i("test", String.valueOf(reply.length()));
											if (reply.length() <= 0) {
												for (int i = 0; i < result.getFulfillment().getMessages().size(); i++) {
													ResponseMessage.ResponseSpeech Messages = (ResponseMessage.ResponseSpeech) result.getFulfillment().getMessages().get(i);
													String key = mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
															.push().getKey();
													DataChatContent botChatMessage = new DataChatContent("RSPbot",
															Messages.getSpeech().toString().replace("[", "").replace("]", ""),
															"가위바위봇",
															null,
															null,
															System.currentTimeMillis(),
															1L,
															key);
													
													mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
															.child(key).setValue(botChatMessage);
													// 마지막 채팅 내용 채팅방에 띄우기 위해 저장 - 내 채팅방
													mDatabaseMyUserRoom.child(ChatRoomKey).child("lastMessage").setValue(botChatMessage.getText());
													mDatabaseMyUserRoom.child(ChatRoomKey).child("lastTime").setValue(botChatMessage.getDateTime());
												}
											} else {
												String key = mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
														.push().getKey();
												DataChatContent botChatMessage = new DataChatContent("RSPbot",
														reply,
														"가위바위봇",
														null,
														null,
														System.currentTimeMillis(),
														1L,
														key);
												mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
														.child(key).setValue(botChatMessage);
												// 마지막 채팅 내용 채팅방에 띄우기 위해 저장 - 내 채팅방
												mDatabaseMyUserRoom.child(ChatRoomKey).child("lastMessage").setValue(botChatMessage.getText());
												mDatabaseMyUserRoom.child(ChatRoomKey).child("lastTime").setValue(botChatMessage.getDateTime());
											}
											
										}
										// 텍스트 다 지우고
										mSendText.setHint("메시지를 입력하세요.");
										mProgressBar.setVisibility(View.GONE);
									}
								}.execute(aiRequest);
							}
						} else if (BotType.equals("Inputbot")) {
							aiService.startListening();
							
							String message = mSendText.getText().toString();
							if (!message.equals("")) {
								aiRequest.setQuery(message);
								new AsyncTask<AIRequest, Void, AIResponse>() {
									
									@Override
									protected void onPreExecute() {
										super.onPreExecute();
										
										mSendText.setHint("입력봇이 당신의 데이터를 읽고 있어요...");
										mSendButton.setEnabled(false);
									}
									
									@Override
									protected AIResponse doInBackground(AIRequest... aiRequests) {
										final AIRequest request = aiRequests[0];
										try {
											Log.d("DialogFlowAsyncTask", "Request" + aiRequest.toString());
											final AIResponse response = aiDataService.request(aiRequest);
											return response;
										} catch (AIServiceException e) {
										}
										return null;
									}
									
									@Override
									protected void onPostExecute(AIResponse response) {
										
										Result result = response.getResult();
										String reply = result.getFulfillment().getSpeech();
										String key = mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
												.push().getKey();
										DataChatContent botChatMessage = new DataChatContent("Inputbot",
												reply,
												"입력봇",
												null,
												null,
												System.currentTimeMillis(),
												2L,
												key);
										mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
												.child(key).setValue(botChatMessage);
										// 마지막 채팅 내용 채팅방에 띄우기 위해 저장 - 내 채팅방
										mDatabaseMyUserRoom.child(ChatRoomKey).child("lastMessage").setValue(botChatMessage.getText());
										mDatabaseMyUserRoom.child(ChatRoomKey).child("lastTime").setValue(botChatMessage.getDateTime());
										
										if (result.getParameters().get("date") != null && result.getParameters().get("MFaccount") != null && result.getParameters().get("MFcategory") != null && result.getParameters().get("number") != null) {
											// key 는 새로운 키 생성
											String mfkey = mDatabase.child("moneyflow").child(mFirebaseUser.getUid()).push().getKey();
											// 데이터베이스에 데이터 목록을 추가
											//데이터가 들어갈 순서 : String type, Long date, String account, String category, Long price, String usage
											String dateString = result.getParameters().get("date").toString();
											dateString = dateString.replace("\"", "");
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
											Date dateinput = new Date();
											try {
												dateinput = sdf.parse(dateString);
											} catch (Exception e) {
												e.printStackTrace();
											}
											Log.d("test", sdf.format(dateinput));
											
											DataMoneyFlowFB newData = new DataMoneyFlowFB("지출",
													dateinput.getTime(),
													result.getParameters().get("MFaccount").getAsString(),
													result.getParameters().get("MFcategory").getAsString(),
													result.getParameters().get("number").getAsLong(),
													result.getParameters().get("MFcategory").getAsString());
											// JSON 에 전달할 수 있는 유형의 Map 만들기
											Map<String, Object> InputValues = newData.toMap();
											
											Map<String, Object> childUpdates = new HashMap<>();
											
											childUpdates.put("/moneyflow/" + mFirebaseUser.getUid() + "/" + mfkey, InputValues);
											// HashMap 데이터베이스에 집어넣기
											mDatabase.updateChildren(childUpdates);
											
											
										}
										// 텍스트 다 지우고
										mSendText.setHint("메시지를 입력하세요.");
										mProgressBar.setVisibility(View.GONE);
									}
									
								}.execute(aiRequest);
							}
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
	
	// 사진 관련.. 아직 제대로 못봣다.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("test", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
		
		if (requestCode == REQUEST_IMAGE) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					final Uri uri = data.getData();
					Log.d("test", "Uri: " + uri.toString());
					
					mDatabaseMyUserRoom.child(ChatRoomKey).addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
							String key = mDatabaseChatRoom.child(ChatRoomKey).child(MESSAGES_CHILD)
									.push().getKey();
							DataChatContent tempMessage = new DataChatContent(mFirebaseUser.getUid(),
									null, mUserNickname, mUserPhoto,
									LOADING_IMAGE_URL, System.currentTimeMillis(),
									dataSnapshot.getValue(DataMessengerUserRoom.class).UserCount,
									key);
							
							
							// 데이터 넣어주고
							mDatabaseChatRoom.child(MESSAGES_CHILD).child(key)
									.setValue(tempMessage, new DatabaseReference.CompletionListener() {
										@Override
										public void onComplete(DatabaseError databaseError,
										                       DatabaseReference databaseReference) {
											if (databaseError == null) {
												String key = databaseReference.getKey();
												StorageReference storageReference =
														FirebaseStorage.getInstance()
																.getReference(mFirebaseUser.getUid())
																.child(key)
																.child(uri.getLastPathSegment());
												
												putImageInStorage(storageReference, uri, key);
											} else {
												Log.w("test", "Unable to write message to database.",
														databaseError.toException());
											}
										}
									});
						}
						
						@Override
						public void onCancelled(@NonNull DatabaseError databaseError) {
						
						}
					});
				}
			}
		}
	}
	
	// 사진 관련. 아직 제대로 못봤다.
	private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
		storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
				new OnCompleteListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
						if (task.isSuccessful()) {
							mDatabaseMyUserRoom.child(ChatRoomKey).addListenerForSingleValueEvent(new ValueEventListener() {
								@Override
								public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
									
									DataChatContent dataChatContent =
											new DataChatContent(mFirebaseUser.getUid(), null, mUserNickname, mUserPhoto,
													task.getResult().getStorage().getDownloadUrl()
															.toString(), System.currentTimeMillis(),
													dataSnapshot.getValue(DataMessengerUserRoom.class).UserCount,
													key);
									mDatabaseChatRoom.child(MESSAGES_CHILD).child(key)
											.setValue(dataChatContent);
								}
								
								@Override
								public void onCancelled(@NonNull DatabaseError databaseError) {
								
								}
							});
						} else {
							Log.w("test", "Image upload task was not successful.",
									task.getException());
						}
					}
				});
	}
	
	@Override
	public void onResult(AIResponse result) {
	
	}
	
	@Override
	public void onError(AIError error) {
	
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
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return super.onSupportNavigateUp();
	}
	
	//	toolbar 에 메뉴 띄워주는 함수
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionmode_setting, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
			case R.id.actionmodeDelete:
				AlertDialog.Builder cancelaction = new AlertDialog.Builder(ChatActivity.this);
				
				cancelaction.setPositiveButton("머무르기", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				
				cancelaction.setNegativeButton("지우기", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
								dataSnapshot.child("UserRooms").child(mFirebaseUser.getUid()).child(ChatRoomKey).getRef().removeValue(new DatabaseReference.CompletionListener() {
									@Override
									public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
										for(String UID : OtherUIDList) {
											dataSnapshot.child("UserRooms").child(UID).child(ChatRoomKey).getRef().removeValue();
										}
										dataSnapshot.child("Messenger").child("chatRoom").child(ChatRoomKey).getRef().removeValue();
										finish();
									}
								});
								
							}
							
							@Override
							public void onCancelled(@NonNull DatabaseError databaseError) {
							
							}
						});
						
					}
				});
				
				AlertDialog cancelpopup = cancelaction.create();
				cancelpopup.setTitle("경고");
				cancelpopup.setMessage("채팅방을 지우시겠습니까?\n지울 경우 모든 데이터가 사라지고,\n나와 대화한 상대방한테도 지워집니다.");
				cancelpopup.show();
				return true;
			
			default:
				return true;
		}
	}
}