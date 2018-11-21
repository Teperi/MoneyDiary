package com.blogspot.teperi31.moneydiary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterDRecycler extends RecyclerView.Adapter<AdapterDRecycler.MyViewHolder> {
	
	ArrayList<DataDiary> DList;
	
	
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
	
	AdapterDRecycler(ArrayList<DataDiary> DList){
		this.DList = DList;
	}
	
	@Override
	public AdapterDRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrow_diary_includeimage, parent, false);
		return new MyViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		holder.reDate.setText(DList.get(position).DListDateString);
		holder.reTitle.setText(DList.get(position).DListTitle);
		holder.reContent.setText(DList.get(position).DListContent);
		holder.reImage.setImageURI(DList.get(position).DListImage);
	}
	
	@Override
	public int getItemCount() {
		return DList.size();
	}
}
