package com.blogspot.teperi31.moneydiary;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderChatUserList extends RecyclerView.ViewHolder {
	CircleImageView profilePhoto;
	TextView profileNickname;
	
	public ViewHolderChatUserList(View v) {
		super(v);
		
		profilePhoto = itemView.findViewById(R.id.messenger_chatlist_row_profileCircleImage);
		profileNickname = itemView.findViewById(R.id.messenger_chatlist_row_profileNicknameText);
	}
	
	public void bindToChatList(DataUser userdata) {
		profileNickname.setText(userdata.NickName);
		if(userdata.Photo !=null) {
			Glide.with(profilePhoto.getContext()).load(Uri.parse(userdata.Photo)).into(profilePhoto);
		} else {
			profilePhoto.setImageResource(R.drawable.cat3);
		}
	}
}