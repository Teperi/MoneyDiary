package com.blogspot.teperi31.moneydiary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
	
	public static class MyViewHolder extends RecyclerView.ViewHolder {
		ImageView ivPicture;
		TextView tvPrice;
		
		MyViewHolder(View view){
			super(view);
			ivPicture = view.findViewById(R.id.re_picture);
			tvPrice = view.findViewById(R.id.re_price);
		}
	}
	
	private ArrayList<FoodInfo> foodInfoArrayList;
	MyAdapter(ArrayList<FoodInfo> foodInfoArrayList){
		this.foodInfoArrayList = foodInfoArrayList;
	}
	
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrow, parent, false);
		
		return new MyViewHolder(v);
	}
	
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		
		MyViewHolder myViewHolder = (MyViewHolder) holder;
		
		myViewHolder.ivPicture.setImageResource(foodInfoArrayList.get(position).drawableId);
		myViewHolder.tvPrice.setText(foodInfoArrayList.get(position).price);
	}
	
	@Override
	public int getItemCount() {
		return foodInfoArrayList.size();
	}
}
