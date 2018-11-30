package com.blogspot.teperi31.moneydiary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/*
* 가계부 데이터를 저장하는 클래스
* */

public class DataMoneyFlow {
	
	
	int MFListId;
	String MFListType;
	Date MFListDate;
	String MFListDateString;
	String MFListAccount;
	String MFListCategory;
	int MFListPrice;
	String MFListUsage;
	
	public DataMoneyFlow(String type, Date date, String account, String category, int price, String usage, int id) {
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		
		this.MFListId = id;
		this.MFListType = type;
		this.MFListDate = date;
		this.MFListAccount = account;
		this.MFListCategory = category;
		this.MFListPrice = price;
		this.MFListUsage = usage;
		this.MFListDateString = sdf.format(MFListDate);
		
	}
	
	public static Comparator<DataMoneyFlow> MoneyFlowDateComparator = new Comparator<DataMoneyFlow>() {
		@Override
		public int compare(DataMoneyFlow o1, DataMoneyFlow o2) {
			return o2.MFListDate.compareTo(o1.MFListDate);
		}
	};
	
}
