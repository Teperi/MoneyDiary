package com.blogspot.teperi31.moneydiary;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AdapterMFRecycler extends RecyclerView.Adapter<AdapterMFRecycler.MyViewHolder> {
	
	Context context;
	
	//클래스 불러오기
	private ArrayList<DataMoneyFlow> MFList;
	List<Integer> selectedIDs = new ArrayList<>();
	
	// 뷰 홀더 만들기
	public static class MyViewHolder extends RecyclerView.ViewHolder {
		TextView reDate;
		TextView reType;
		TextView reAccount;
		TextView reCategory;
		TextView reUsage;
		TextView rePrice;
		CardView childView;
		
		
		
		
		MyViewHolder(View view) {
			super(view);
			reDate = view.findViewById(R.id.moneyflow_re_date);
			reType = view.findViewById(R.id.moneyflow_re_type);
			reAccount = view.findViewById(R.id.moneyflow_re_account);
			reCategory = view.findViewById(R.id.moneyflow_re_category);
			reUsage = view.findViewById(R.id.moneyflow_re_usage);
			rePrice = view.findViewById(R.id.moneyflow_re_price);
			childView = view.findViewById(R.id.moneyflow_list_clickforview);
			
		}
	}
		
		
		// 연결할 데이터목록
		AdapterMFRecycler(Context context, ArrayList<DataMoneyFlow> MFList) {
			this.context = context;
			this.MFList = MFList;
		}
		
		
		// 뷰 홀더에 들어온 아이템 늘려주는 도구
		@Override
		public AdapterMFRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			
			View v = LayoutInflater.from(context).inflate(R.layout.moneyflow_list_row, parent, false);
			
			return new MyViewHolder(v);
		}
		
		// 뷰 홀더와 데이터 연결
		@Override
		public void onBindViewHolder(final MyViewHolder holder, final int position) {
			
			holder.reDate.setText(MFList.get(position).MFListDateString);
			holder.reType.setText(MFList.get(position).MFListType);
			holder.reAccount.setText(MFList.get(position).MFListAccount);
			holder.reCategory.setText(MFList.get(position).MFListCategory);
			holder.reUsage.setText(MFList.get(position).MFListUsage);
			holder.rePrice.setText(String.valueOf(MFList.get(position).MFListPrice));
			
			if (MFList.get(position).MFListType.equals("지출")) {
				holder.rePrice.setTextColor(Color.parseColor("#B00020"));
			} else if (MFList.get(position).MFListType.equals("수입")) {
				holder.rePrice.setTextColor(Color.parseColor("#0086c9"));
			} else {
				holder.rePrice.setTextColor(Color.parseColor("#000000"));
			}
			
			int id = MFList.get(position).MFListId;
			
			// 롱클릭시 색상 조정
			if(selectedIDs.contains(id)){
				holder.childView.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorAccent)));
			} else {
				holder.childView.setBackground(new ColorDrawable(ContextCompat.getColor(context, android.R.color.background_light)));
			}
			
			
		}
		
		@Override
		public int getItemCount() {
			return MFList.size();
		}
	
	public DataMoneyFlow getItem(int position) {
		return MFList.get(position);
	}
	
	// 아이디 선택 여부 저장
	public void setSelectedIDs(List<Integer> selectedIds) {
		this.selectedIDs = selectedIds;
		notifyDataSetChanged();
	}
		
		
	}
