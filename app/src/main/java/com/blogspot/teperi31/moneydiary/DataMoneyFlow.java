package com.blogspot.teperi31.moneydiary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataMoneyFlow {
	String MFListType;
	Date MFListDate;
	String MFListDateString;
	String MFListAccount;
	String MFListCategory;
	int MFListPrice;
	String MFListUsage;
	
	public DataMoneyFlow(String type, Date date, String account, String category, int price, String usage) {
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		
		this.MFListType = type;
		this.MFListDate = date;
		this.MFListAccount = account;
		this.MFListCategory = category;
		this.MFListPrice = price;
		this.MFListUsage = usage;
		this.MFListDateString = sdf.format(MFListDate);
		
	}
	
}
