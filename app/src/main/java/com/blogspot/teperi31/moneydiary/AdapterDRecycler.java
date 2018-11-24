package com.blogspot.teperi31.moneydiary;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdapterDRecycler extends RecyclerView.Adapter<AdapterDRecycler.MyViewHolder> {
	
	Context context;
	ArrayList<DataDiary> DList;
	List<Integer> selectedIDs = new ArrayList<>();
	
	// 뷰 홀더와 layout 연결
	public static class MyViewHolder extends RecyclerView.ViewHolder  {
		TextView reDate;
		TextView reTitle;
		ImageView reImage;
		TextView reContent;
		CardView childView;
		
		
		MyViewHolder(View view) {
			super(view);
			reDate = view.findViewById(R.id.diary_re_date_inImage);
			reTitle = view.findViewById(R.id.diary_re_title_inImage);
			reImage = view.findViewById(R.id.diary_re_image_inImage);
			reContent = view.findViewById(R.id.diary_re_content_inImage);
			childView = view.findViewById(R.id.diary_re_childView);
		}
		
		
	}
	
	// 어뎁터로 데이터 list 연결
	AdapterDRecycler(Context context, ArrayList<DataDiary> DList){
		this.context = context;
		this.DList = DList;
	}
	
	// 뷰 홀더 불리기
	@Override
	public AdapterDRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(context).inflate(R.layout.recyclerviewrow_diary_includeimage, parent, false);
		return new MyViewHolder(itemView);
	}
	
	
	// 뷰홀더와 데이터 연결
	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		holder.reDate.setText(DList.get(position).DListDateString);
		holder.reTitle.setText(DList.get(position).DListTitle);
		holder.reContent.setText(DList.get(position).DListContent);
		holder.reImage.setImageURI(DList.get(position).DListImage);
		
		int id = DList.get(position).id;
		
		// 롱클릭시 색상 조정
		if(selectedIDs.contains(id)){
			holder.childView.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorAccent)));
		} else {
			holder.childView.setBackground(new ColorDrawable(ContextCompat.getColor(context, android.R.color.background_light)));
		}
		
	}
	
	// 개수 확인
	@Override
	public int getItemCount() {
		return DList.size();
	}
	
	public DataDiary getItem(int position) {
		return DList.get(position);
	}
	
	// 아이디 선택 여부 저장
	public void setSelectedIDs(List<Integer> selectedIds) {
		this.selectedIDs = selectedIds;
		notifyDataSetChanged();
	}
	
	
	
	
	
	
}
