package com.blogspot.teperi31.moneydiary;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderChatRoomList extends RecyclerView.ViewHolder {
	CircleImageView profileImage;
	TextView chatRoomTitle;
	TextView lastMessage;
	TextView unReadCount;
	TextView lastMessageTime;
	TextView userCount;
	ImageView userGroupImage;
	
	public ViewHolderChatRoomList(View itemView) {
		super(itemView);
		
		profileImage = itemView.findViewById(R.id.messenger_chatlist_row_profileCircleImage);
		chatRoomTitle = itemView.findViewById(R.id.messenger_chatlist_row_profileNicknameText);
		lastMessage = itemView.findViewById(R.id.messenger_chatlist_row_LastMessageText);
		lastMessageTime = itemView.findViewById(R.id.messenger_chatlist_row_lastTimeText);
		unReadCount = itemView.findViewById(R.id.messenger_chatlist_row_UnReadMessagesCountText);
		userCount = itemView.findViewById(R.id.messenger_chatlist_row_groupUserCountText);
		userGroupImage = itemView.findViewById(R.id.messenger_chatlist_row_groupUserImage);
		lastMessage.setVisibility(View.VISIBLE);
		unReadCount.setVisibility(View.VISIBLE);
		lastMessageTime.setVisibility(View.VISIBLE);
	}
	
	public void bindToChatRoomList(DataMessengerUserRoom Data) {
		// 시간을 받기 위한 포멧
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm",Locale.KOREA);
		
		//TODO : 이미지 프로필 이미지로 받아오기
		profileImage.setImageResource(R.drawable.previewimage);
		chatRoomTitle.setText(Data.title);
		if(Data.RoomType.equals("Group")){
			userCount.setVisibility(View.VISIBLE);
			userGroupImage.setVisibility(View.VISIBLE);
			userCount.setText(String.valueOf(Data.UserCount));
		} else {
			userCount.setVisibility(View.GONE);
			userGroupImage.setVisibility(View.GONE);
		}
		if(Data.lastMessage == null) {
			lastMessage.setText("");
			lastMessageTime.setText("");
		} else {
			lastMessage.setText(Data.lastMessage);
			lastMessageTime.setText(String.valueOf(sdf.format(new Date(Data.lastTime))));
		}
		if(Data.UnReadMessageCount == null) {
			unReadCount.setVisibility(View.GONE);
		} else {
			unReadCount.setVisibility(View.VISIBLE);
			unReadCount.setText(String.valueOf(Data.UnReadMessageCount));
		}
		
	}
}
