package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdapterMoneyFlowList extends FirebaseRecyclerAdapter<DataMoneyFlowFB,AdapterMoneyFlowList.ViewHolderMoneyFlowFB> {
	
	public static class ViewHolderMoneyFlowFB extends RecyclerView.ViewHolder {
		TextView DateView;
		TextView TypeView;
		TextView AccountView;
		TextView CategoryView;
		TextView UsageView;
		TextView PriceView;
		
		ViewHolderMoneyFlowFB(View itemView) {
			super(itemView);
			
			DateView = itemView.findViewById(R.id.moneyflow_re_date);
			TypeView = itemView.findViewById(R.id.moneyflow_re_type);
			AccountView = itemView.findViewById(R.id.moneyflow_re_account);
			CategoryView = itemView.findViewById(R.id.moneyflow_re_category);
			UsageView = itemView.findViewById(R.id.moneyflow_re_usage);
			PriceView = itemView.findViewById(R.id.moneyflow_re_price);
			
			
		}
	}
	
	public AdapterMoneyFlowList(@NonNull FirebaseRecyclerOptions<DataMoneyFlowFB> options) {
		super(options);
	}
	@NonNull
	@Override
	public ViewHolderMoneyFlowFB onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.moneyflow_list_row, viewGroup, false);
		return new ViewHolderMoneyFlowFB(itemView);
	}
	
	@Override
	protected void onBindViewHolder(final ViewHolderMoneyFlowFB holder, int position, DataMoneyFlowFB model) {
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		Date MFcalender = new Date(model.date);
		
		holder.DateView.setText(sdf.format(MFcalender));
		holder.TypeView.setText(model.type);
		holder.AccountView.setText(model.account);
		holder.CategoryView.setText(model.category);
		holder.UsageView.setText(model.usage);
		holder.PriceView.setText(toNumFormat(model.price));
		
		if(model.type.equals("수입")) {
			holder.PriceView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimaryDark));
		} else if (model.type.equals("지출")) {
			holder.PriceView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.colorError));
		} else {
			holder.PriceView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.colorBlack));
		}
		
		// 키값 저장해서 에디트 페이지 갈 때 키값 넣어주기
		DatabaseReference MFref = getRef(position);
		final String MFKey = MFref.getKey();
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), EditMoneyFlowDataFB.class);
				intent.putExtra(EditMoneyFlowDataFB.EXTRA_MFDATA_KEY, MFKey);
				holder.itemView.getContext().startActivity(intent);
			}
		});
	}
	
	// 1000 단위에 쉼표 찍어주는 함수
	private static String toNumFormat(Long num) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num);
	}
	
	@Override
	public void onDataChanged() {
		super.onDataChanged();
	}
}
