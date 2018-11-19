package com.blogspot.teperi31.moneydiary;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterMFRecycler extends RecyclerView.Adapter<AdapterMFRecycler.MyViewHolder>  {
	
	//클래스 불러오기
	private ArrayList<DataMoneyFlow> MFList;
	
	
	public static class MyViewHolder extends RecyclerView.ViewHolder {
		TextView reDate;
		TextView reType;
		TextView reAccount;
		TextView reCategory;
		TextView reUsage;
		TextView rePrice;
		
		MyViewHolder(View view){
			super(view);
			reDate = view.findViewById(R.id.moneyflow_re_date);
			reType = view.findViewById(R.id.moneyflow_re_type);
			reAccount = view.findViewById(R.id.moneyflow_re_account);
			reCategory = view.findViewById(R.id.moneyflow_re_category);
			reUsage = view.findViewById(R.id.moneyflow_re_usage);
			rePrice = view.findViewById(R.id.moneyflow_re_price);
		}
	}
	
	AdapterMFRecycler(ArrayList<DataMoneyFlow> MFList){
		this.MFList = MFList;
	}
	
	@Override
	public AdapterMFRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrow_moneyflow, parent, false);
		
		return new MyViewHolder(v);
	}
	
	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		
		holder.reDate.setText(MFList.get(position).MFListDate);
		holder.reType.setText(MFList.get(position).MFListType);
		holder.reAccount.setText(MFList.get(position).MFListAccount);
		holder.reCategory.setText(MFList.get(position).MFListCategory);
		holder.reUsage.setText(MFList.get(position).MFListUsage);
		holder.rePrice.setText(String.valueOf(MFList.get(position).MFListPrice));
		
		if(MFList.get(position).MFListType.equals("출금")){
			holder.rePrice.setTextColor(Color.parseColor("#c62828"));
		} else if (MFList.get(position).MFListType.equals("입금")){
			holder.rePrice.setTextColor(Color.parseColor("#1a237e"));
		}
		
	
	}
	
	@Override
	public int getItemCount() {
		return MFList.size();
	}
}
