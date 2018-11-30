package com.blogspot.teperi31.moneydiary;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewHolderMoneyFlow extends RecyclerView.ViewHolder {
	TextView DateView;
	TextView TypeView;
	TextView AccountView;
	TextView CategoryView;
	TextView UsageView;
	TextView PriceView;
	
	public ViewHolderMoneyFlow(@NonNull View itemView) {
		super(itemView);
		
		DateView = itemView.findViewById(R.id.moneyflow_re_date);
		TypeView = itemView.findViewById(R.id.moneyflow_re_type);
		AccountView = itemView.findViewById(R.id.moneyflow_re_account);
		CategoryView = itemView.findViewById(R.id.moneyflow_re_category);
		UsageView = itemView.findViewById(R.id.moneyflow_re_usage);
		PriceView = itemView.findViewById(R.id.moneyflow_re_price);
		
		
	}
	
	public void bindToMoneyFlow(DataMoneyFlowFB Data) {
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		Date MFcalender = new Date(Data.date);
		
		DateView.setText(sdf.format(MFcalender));
		TypeView.setText(Data.type);
		AccountView.setText(Data.account);
		CategoryView.setText(Data.category);
		UsageView.setText(Data.usage);
		PriceView.setText(String.valueOf(Data.price));
	}
}
