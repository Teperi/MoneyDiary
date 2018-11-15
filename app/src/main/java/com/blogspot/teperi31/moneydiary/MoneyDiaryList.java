package com.blogspot.teperi31.moneydiary;


import java.io.Serializable;

public class MoneyDiaryList implements Serializable {
	
	String MDListId;
	String MdListType;
	int MDListDateYear;
	int MDListDateMonth;
	int MDListDateDay;
	String MDListDate;
	String MDListAccount;
	String MDListCategory;
	int MDListPrice;
	String MDListUsage;
	
	public MoneyDiaryList(String id, String type, int year, int month, int day, String account, String category, int price, String usage) {
		this.MDListId = id;
		this.MdListType = type;
		this.MDListDateYear = year;
		this.MDListDateMonth = month;
		this.MDListDateDay = day;
		this.MDListAccount = account;
		this.MDListCategory = category;
		this.MDListPrice = price;
		this.MDListUsage = usage;
		this.MDListDate = (String) (year + "-" + month + "-" + day);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
