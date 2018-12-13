package com.blogspot.teperi31.moneydiary;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessengerGroupCreateList extends RecyclerView.Adapter<AdapterMessengerGroupCreateList.myViewHolder> {
	
	ArrayList<DataUser> UList;
	FirebaseUser User;
	DatabaseReference mDatabase;
	Context context;
	ArrayList<DataUser> CheckedUserList = new ArrayList<>();
	
	// 뷰 홀더와 layout 연결
	public static class myViewHolder extends RecyclerView.ViewHolder {
		CircleImageView ProfileImage;
		TextView ProfileName;
		AppCompatCheckBox CheckBox;
		myViewHolder(View view) {
			super(view);
			ProfileImage = view.findViewById(R.id.messenger_chatlist_groupcreate_row_profileCircleImage);
			ProfileName = view.findViewById(R.id.messenger_chatlist_groupcreate_row_profileNicknameText);
			CheckBox = view.findViewById(R.id.messenger_chatlist_groupcreate_row_checkbox);
			CheckBox.setVisibility(View.VISIBLE);
		}
	}
	
	AdapterMessengerGroupCreateList(Context context, ArrayList<DataUser> UserList) {
		this.context = context;
		this.UList = UserList;
	}
	
	@Override
	public int getItemCount() {
		return UList.size();
	}
	
	// 틀 만들기
	@Override
	public AdapterMessengerGroupCreateList.myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messenger_chatlist_groupcreate_row, viewGroup, false);
		return new AdapterMessengerGroupCreateList.myViewHolder(itemView);
	}
	
	// 틀 묶기
	@Override
	public void onBindViewHolder(@NonNull final AdapterMessengerGroupCreateList.myViewHolder holder, final int i) {
		// TODO : 사진 개인별 사진으로 바꿔주는 기능 추가해야 함
		holder.ProfileImage.setImageResource(R.drawable.cat3);
		// 내 닉네임 가져오기
		holder.ProfileName.setText(UList.get(i).NickName);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(holder.CheckBox.isChecked()) {
					holder.CheckBox.setChecked(false);
				} else {
					holder.CheckBox.setChecked(true);
				}
				
			}
		});
		
		holder.CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					CheckedUserList.add(UList.get(i));
					holder.itemView.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorAccent)));
				} else {
					CheckedUserList.remove(UList.get(i));
					holder.itemView.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorWhite)));
				}
			}
		});
	}
}
