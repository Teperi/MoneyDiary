package com.blogspot.teperi31.moneydiary;


import java.io.Serializable;

public class DataMoneyFlow implements Serializable {
	String MFListType;
	int MFListDateYear;
	int MFListDateMonth;
	int MFListDateDay;
	String MFListDate;
	String MFListAccount;
	String MFListCategory;
	int MFListPrice;
	String MFListUsage;
	
	public DataMoneyFlow(String type, int year, int month, int day, String account, String category, int price, String usage) {
		this.MFListType = type;
		this.MFListDateYear = year;
		this.MFListDateMonth = month;
		this.MFListDateDay = day;
		this.MFListAccount = account;
		this.MFListCategory = category;
		this.MFListPrice = price;
		this.MFListUsage = usage;
		if (day < 10){
			this.MFListDate = (String.valueOf(year) + "-" + String.valueOf(month) + "-" + "0" + String.valueOf(day));
		} else {
			this.MFListDate = (String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day));
		}
		
	}
	
}
