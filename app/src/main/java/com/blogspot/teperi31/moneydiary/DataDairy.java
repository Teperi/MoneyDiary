package com.blogspot.teperi31.moneydiary;

import java.io.Serializable;

public class DataDairy implements Serializable {
	int DListDateYear;
	int DListDateMonth;
	int DListDateDay;
	String DListDate;
	String DListTitle;
	String DListContent;
	int DListImage;
	
	public DataDairy(int year, int month, int day, String title, String content, int image){
		this.DListDateYear = year;
		this.DListDateMonth = month;
		this.DListDateDay = day;
		this.DListTitle = title;
		this.DListContent = content;
		this.DListImage = image;
		
		if (day < 10){
			this.DListDate = (String.valueOf(year) + "-" + String.valueOf(month) + "-" + "0" + String.valueOf(day));
		} else {
			this.DListDate = (String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day));
		}
	}
	
}
