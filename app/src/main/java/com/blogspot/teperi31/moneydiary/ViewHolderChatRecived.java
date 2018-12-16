package com.blogspot.teperi31.moneydiary;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderChatRecived extends RecyclerView.ViewHolder {
	TextView messageTextView;
	ImageView messageImageView;
	TextView messageTimeView;
	TextView messengerTextView;
	CircleImageView messengerImageView;
	TextView messengerUnreadCountTextView;
	
	public ViewHolderChatRecived(View v) {
		super(v);
		messageTextView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_contentText);
		messageImageView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_contentImage);
		messageTimeView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_time);
		messengerTextView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_userNickText);
		messengerImageView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_profileImage);
		messengerUnreadCountTextView = itemView.findViewById(R.id.messenger_chatcontent_recived_row_unreadCoutText);
	}
	
	public void bindToChat(DataChatContent usermessage , String ChatRoomKey) {
		//텍스트가 있는 경우 텍스트 연결
		if (usermessage.getText() != null) {
			messageTextView.setText(usermessage.getText());
			messageTextView.setVisibility(TextView.VISIBLE);
			messageImageView.setVisibility(ImageView.GONE);
		}
		// 사진이 있는 경우 사진 연결
		else if (usermessage.getImageUrl() != null) {
			String imageUrl = usermessage.getImageUrl();
			if (imageUrl.startsWith("gs://")) {
				StorageReference storageReference = FirebaseStorage.getInstance()
						.getReferenceFromUrl(imageUrl);
				storageReference.getDownloadUrl().addOnCompleteListener(
						new OnCompleteListener<Uri>() {
							@Override
							public void onComplete(@NonNull Task<Uri> task) {
								if (task.isSuccessful()) {
									String downloadUrl = task.getResult().toString();
									Glide.with(messageImageView.getContext())
											.load(downloadUrl)
											.into(messageImageView);
								} else {
									Log.w("test", "Getting download url was not successful.",
											task.getException());
								}
							}
						});
			} else {
				Glide.with(messageImageView.getContext())
						.load(usermessage.getImageUrl())
						.into(messageImageView);
			}
			messageImageView.setVisibility(ImageView.VISIBLE);
			messageTextView.setVisibility(TextView.GONE);
		}
		messengerTextView.setText(usermessage.getName());
		if (usermessage.getPhotoUrl() == null) {
			messengerImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),
					R.drawable.cat3));
		} else {
			Glide.with(itemView.getContext())
					.load(usermessage.getPhotoUrl())
					.into(messengerImageView);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		messageTimeView.setText(sdf.format(new Date(usermessage.getDateTime())));
		
		if(usermessage.UnReadUserCount == 0) {
			messengerUnreadCountTextView.setText("");
			messengerUnreadCountTextView.setVisibility(TextView.GONE);
		} else {
			messengerUnreadCountTextView.setVisibility(TextView.VISIBLE);
			messengerUnreadCountTextView.setText(String.valueOf(usermessage.UnReadUserCount));
		}
		
		setReadCounter(usermessage, ChatRoomKey, usermessage.messageKey);
	}
	
	// 채팅 읽은 사람 수 계산해주는 메소드
	// FirebaseRecyclerView 를 통해 변환된 데이터와 채팅 목록에서 Intent로 넘어온 채팅방 Key 값을 받아서 사용
	void setReadCounter(final DataChatContent usermessage, final String ChatRoomKey, final String MessageKey) {
		if(usermessage.getId().equals("chatbot")) {
		
		} else {
			// 현재 유저 정보 저장
			final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
			// 만약 이 채팅을 읽은 사람 목록에 내가 없다면
			if (!usermessage.ReadUsers.containsKey(mUser.getUid())) {
				//ReadUser 목록이 있는 곳으로 접근
				FirebaseDatabase.getInstance().getReference()
						.child("Messenger/chatRoom").child(ChatRoomKey)
						.child("messages").child(MessageKey).child("ReadUsers")
						.addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
								// ReadUser 목록을 저장
								Map<String, Boolean> readUsers = (Map<String, Boolean>) dataSnapshot.getValue();
								// 내 UID 추가
								readUsers.put(mUser.getUid(), true);
								// ReadUser 목록 교체(내 UID 를 추가해서 저장)
								FirebaseDatabase.getInstance().getReference()
										.child("Messenger/chatRoom").child(ChatRoomKey)
										.child("messages").child(MessageKey).child("ReadUsers")
										.setValue(readUsers);
								
								// 현재 읽은 사람 수 에서 내가 읽었기 때문에 1을 뺌
								Long count = usermessage.UnReadUserCount - 1;
								
								// 그 값을 데이터베이스에 저장
								FirebaseDatabase.getInstance().getReference()
										.child("Messenger/chatRoom").child(ChatRoomKey)
										.child("messages").child(MessageKey).child("UnReadUserCount").setValue(count);
								
							}
							
							@Override
							public void onCancelled(@NonNull DatabaseError databaseError) {
							
							}
						});
			}
		}
	}
}
