package com.blogspot.teperi31.moneydiary;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdapterDRecycler extends RecyclerView.Adapter<AdapterDRecycler.MyViewHolder> {
	
	ArrayList<DataDiary> DList;
	
	// 뷰 홀더와 layout 연결
	public static class MyViewHolder extends RecyclerView.ViewHolder {
		TextView reDate;
		TextView reTitle;
		ImageView reImage;
		TextView reContent;
		
		MyViewHolder(View view) {
			super(view);
			reDate = view.findViewById(R.id.diary_re_date_inImage);
			reTitle = view.findViewById(R.id.diary_re_title_inImage);
			reImage = view.findViewById(R.id.diary_re_image_inImage);
			reContent = view.findViewById(R.id.diary_re_content_inImage);
			
		}
	}
	
	// 어뎁터로 데이터 list 연결
	AdapterDRecycler(ArrayList<DataDiary> DList){
		this.DList = DList;
	}
	
	// 뷰 홀더 불리기
	@Override
	public AdapterDRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrow_diary_includeimage, parent, false);
		return new MyViewHolder(itemView);
	}
	
	
	// 뷰홀더와 데이터 연결
	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		holder.reDate.setText(DList.get(position).DListDateString);
		holder.reTitle.setText(DList.get(position).DListTitle);
		holder.reContent.setText(DList.get(position).DListContent);
		holder.reImage.setImageURI(DList.get(position).DListImage);
		
		// 클릭값 받아오기
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				Toast.makeText(context,position+"", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(context, EditDiarydata.class);
				i.putExtra("position",position);
				context.startActivity(i);
			}
		});
		
		
	}
	
	// 개수 확인
	@Override
	public int getItemCount() {
		return DList.size();
	}
}
