package com.blogspot.teperi31.moneydiary;

import android.net.Uri;
import android.support.annotation.NonNull;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ViewHolderChatSent extends RecyclerView.ViewHolder {
	TextView messageTextView;
	ImageView messageImageView;
	TextView messageTimeView;
	TextView messengerUnreadCountTextView;
	
	public ViewHolderChatSent(View v) {
		super(v);
		messageTextView = itemView.findViewById(R.id.messenger_chatcontent_sent_row_contentText);
		messageImageView = itemView.findViewById(R.id.messenger_chatcontent_sent_row_contentImage);
		messageTimeView = itemView.findViewById(R.id.messenger_chatcontent_sent_row_time);
		messengerUnreadCountTextView = itemView.findViewById(R.id.messenger_chatcontent_sent_row_unreadCoutText);
	}
	
	public void bindToChat(DataChatContent usermessage, String ChatRoomKey) {
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
		// 시간 데이터 가져오기
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		messageTimeView.setText(sdf.format(new Date(usermessage.getDateTime())));
		if (usermessage.UnReadUserCount == 0) {
			messengerUnreadCountTextView.setText("");
			messengerUnreadCountTextView.setVisibility(TextView.GONE);
		} else {
			messengerUnreadCountTextView.setVisibility(TextView.VISIBLE);
			messengerUnreadCountTextView.setText(String.valueOf(usermessage.UnReadUserCount));
		}
		
		setReadCounter(usermessage, ChatRoomKey, usermessage.messageKey);
	}
	
	
	void setReadCounter(final DataChatContent usermessage, final String ChatRoomKey, final String MessageKey) {
		final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
		
		if (!usermessage.ReadUsers.containsKey(mUser.getUid())) {
			
			FirebaseDatabase.getInstance().getReference()
					.child("Messenger/chatRoom").child(ChatRoomKey)
					.child("messages").child(MessageKey).child("ReadUsers")
					.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
							Log.d("test", dataSnapshot.getKey());
							Map<String, Boolean> readUsers = (Map<String, Boolean>) dataSnapshot.getValue();
							readUsers.put(mUser.getUid(), true);
							
							FirebaseDatabase.getInstance().getReference()
									.child("Messenger/chatRoom").child(ChatRoomKey)
									.child("messages").child(MessageKey).child("ReadUsers")
									.setValue(readUsers);
							Long count = usermessage.UnReadUserCount - 1;
							
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