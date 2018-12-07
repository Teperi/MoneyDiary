package com.blogspot.teperi31.moneydiary;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderChatRoomList extends RecyclerView.ViewHolder {
	CircleImageView profileImage;
	TextView chatRoomTitle;
	TextView lastMessage;
	TextView unReadCount;
	
	public ViewHolderChatRoomList(View itemView) {
		super(itemView);
		
		profileImage = itemView.findViewById(R.id.messenger_chatlist_row_profileCircleImage);
		chatRoomTitle = itemView.findViewById(R.id.messenger_chatlist_row_profileNicknameText);
		lastMessage = itemView.findViewById(R.id.messenger_chatlist_row_LastMessageText);
		unReadCount = itemView.findViewById(R.id.messenger_chatlist_row_UnReadMessagesCountText);
		lastMessage.setVisibility(View.VISIBLE);
		unReadCount.setVisibility(View.VISIBLE);
	}
	
	public void bindToChatRoomList(DataMessengerUserRoom Data) {
		//TODO : 이미지 프로필 이미지로 받아오기
		profileImage.setImageResource(R.drawable.cat3);
		chatRoomTitle.setText(Data.title);
		if(Data.lastMessage == null) {
			lastMessage.setText("");
		} else {
			lastMessage.setText(Data.lastMessage);
		}
		if(Data.UnReadMessageCount == null) {
			unReadCount.setVisibility(View.GONE);
		} else {
			unReadCount.setText(String.valueOf(Data.UnReadMessageCount));
		}
		
	}
}
