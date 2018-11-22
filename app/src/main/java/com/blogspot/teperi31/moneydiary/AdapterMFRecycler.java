package com.blogspot.teperi31.moneydiary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class AdapterMFRecycler extends RecyclerView.Adapter<AdapterMFRecycler.MyViewHolder> {
	
	Context context;
	
	//클래스 불러오기
	private ArrayList<DataMoneyFlow> MFList;
	
	// 뷰 홀더 만들기
	public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
		TextView reDate;
		TextView reType;
		TextView reAccount;
		TextView reCategory;
		TextView reUsage;
		TextView rePrice;
		TextView buttonViewOption;
		
		
		MyViewHolder(View view) {
			super(view);
			reDate = view.findViewById(R.id.moneyflow_re_date);
			reType = view.findViewById(R.id.moneyflow_re_type);
			reAccount = view.findViewById(R.id.moneyflow_re_account);
			reCategory = view.findViewById(R.id.moneyflow_re_category);
			reUsage = view.findViewById(R.id.moneyflow_re_usage);
			rePrice = view.findViewById(R.id.moneyflow_re_price);
			buttonViewOption = view.findViewById(R.id.textViewOptions);
			
			view.setOnCreateContextMenuListener(this);
		}
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
			MenuInflater popup = new MenuInflater(v.getContext());
			popup.inflate(R.menu.recyclerview_context_menu,menu);
		}
	}
	
	
	// 연결할 데이터목록
	AdapterMFRecycler(ArrayList<DataMoneyFlow> MFList) {
		this.MFList = MFList;
	}
	
	
	// 뷰 홀더에 들어온 아이템 늘려주는 도구
	@Override
	public AdapterMFRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrow_moneyflow, parent, false);
		
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
		
		if (MFList.get(position).MFListType.equals("출금")) {
			holder.rePrice.setTextColor(Color.parseColor("#c62828"));
		} else if (MFList.get(position).MFListType.equals("입금")) {
			holder.rePrice.setTextColor(Color.parseColor("#1a237e"));
		}
		
		holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				context = v.getContext();
				PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
				popup.inflate(R.menu.recyclerview_context_menu);
				
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
							case R.id.itemEdit:
								Toast.makeText(context, "수정", Toast.LENGTH_SHORT).show();
								return true;
							case R.id.itemDelete:
								MFList.remove(position);
								Intent i = new Intent(context,context.getClass());
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(i);
								return true;
							default:
								return false;
						}
					}
				});
				popup.show();
				
			}
		});
		
	}
	
	@Override
	public int getItemCount() {
		return MFList.size();
	}
	
	
}
