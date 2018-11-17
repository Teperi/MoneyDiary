package com.blogspot.teperi31.moneydiary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  {
	
	//클래스 불러오기
	private ArrayList<MoneyDiaryList> MDList;
	
	
	public static class MyViewHolder extends RecyclerView.ViewHolder {
		TextView reDate;
		TextView reType;
		TextView reAccount;
		TextView reCategory;
		TextView reUsage;
		TextView rePrice;
		
		MyViewHolder(View view){
			super(view);
			reDate = view.findViewById(R.id.re_date);
			reType = view.findViewById(R.id.re_type);
			reAccount = view.findViewById(R.id.re_account);
			reCategory = view.findViewById(R.id.re_category);
			reUsage = view.findViewById(R.id.re_usage);
			rePrice = view.findViewById(R.id.re_price);
		}
	}
	
	MyAdapter(ArrayList<MoneyDiaryList> MDList){
		this.MDList = MDList;
	}
	
	@Override
	public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrow, parent, false);
		
		return new MyViewHolder(v);
	}
	
	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		
		holder.reDate.setText(MDList.get(position).MDListDate);
		holder.reType.setText(MDList.get(position).MdListType);
		holder.reAccount.setText(MDList.get(position).MDListAccount);
		holder.reCategory.setText(MDList.get(position).MDListCategory);
		holder.reUsage.setText(MDList.get(position).MDListUsage);
		holder.rePrice.setText(String.valueOf(MDList.get(position).MDListPrice));
	}
	
	@Override
	public int getItemCount() {
		return MDList.size();
	}
}
