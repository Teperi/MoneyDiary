package com.blogspot.teperi31.moneydiary;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessengerUserList extends RecyclerView.Adapter<AdapterMessengerUserList.myViewHolder> {
	
	ArrayList<DataUser> UList;
	
	// 뷰 홀더와 layout 연결
	public static class myViewHolder extends RecyclerView.ViewHolder  {
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
	
	@Override
	public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messenger_chatlist_row, viewGroup, false);
		return new myViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(@NonNull final myViewHolder holder, final int i) {
		holder.ProfileImage.setImageResource(R.drawable.cat3);
		holder.ProfileName.setText(UList.get(i).NickName);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(holder.itemView.getContext(), getItem(i), Toast.LENGTH_SHORT).show();
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
}
